package io.github.vehkiya.service.parser;

import io.github.vehkiya.config.ServiceParserProperties;
import io.github.vehkiya.data.model.domain.Item;
import io.github.vehkiya.service.DataProvider;
import io.github.vehkiya.service.TextParser;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Service
public class LuceneTextParser implements TextParser {

    @Autowired
    private DataProvider dataProvider;

    @Autowired
    private ServiceParserProperties serviceParserProperties;

    private Analyzer analyzer;

    private Directory directory;

    private Pattern pattern;

    private static final String ITEM_NAME_FIELD = "itemName";

    @PostConstruct
    public void init() throws IOException {
        var luceneDir = Paths.get(serviceParserProperties.getIndexPath(), "lucene.dir");
        analyzer = new StandardAnalyzer();
        directory = new MMapDirectory(luceneDir);
        pattern = Pattern.compile(serviceParserProperties.getPattern());
        indexItems();
    }

    private void indexItems() throws IOException {
        var indexWriterConfig = new IndexWriterConfig(analyzer);
        var indexWriter = new IndexWriter(directory, indexWriterConfig);
        indexWriter.deleteAll();
        for (String itemName : dataProvider.itemsCache().keySet()) {
            var document = new Document();
            document.add(new TextField(ITEM_NAME_FIELD, itemName, Field.Store.YES));
            indexWriter.addDocument(document);
        }
        log.info("Indexed {} items", indexWriter.getDocStats().numDocs);
        indexWriter.close();
    }

    @Override
    public boolean messageMatchesPattern(String text) {
        return pattern.matcher(text).find();
    }

    @Override
    public Set<Item> parseItemsFromText(String text) {
        var matcher = pattern.matcher(text);
        Set<String> buffer = new HashSet<>();
        while (matcher.find()) {
            String group = matcher.group();
            Set<String> itemNames = findAllMatchingItemNamesForTerm(group);
            buffer.addAll(itemNames);
        }
        return buffer.stream()
                .map(name -> dataProvider.findByName(name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<String> findAllMatchingItemNamesForTerm(String term) {
        try {
            var threshold = serviceParserProperties.getThreshold();
            var indexReader = DirectoryReader.open(directory);
            var indexSearcher = new IndexSearcher(indexReader);
            var parser = new QueryParser(ITEM_NAME_FIELD, analyzer);
            parser.setDefaultOperator(QueryParser.Operator.AND);
            var query = parser.parse(term + "~10");
            ScoreDoc[] hits = indexSearcher.search(query, 10).scoreDocs;
            return Arrays.stream(hits)
                    .filter(hit -> hit.score > threshold)
                    .map(hit -> getDocumentText(hit.doc, indexReader))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

        } catch (IOException | ParseException e) {
            log.error("Failed to find items for term {}", term);
            return Collections.emptySet();
        }
    }

    private String getDocumentText(int docId, IndexReader indexReader) {
        try {
            var document = indexReader.document(docId);
            return document.get(ITEM_NAME_FIELD);
        } catch (IOException e) {
            log.error("Error while trying to retrieve document with id {}", docId, e);
            return null;
        }
    }
}
