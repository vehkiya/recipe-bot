package io.github.vehkiya.service.parser

import io.github.vehkiya.config.ServiceParserProperties
import io.github.vehkiya.data.model.domain.Item
import io.github.vehkiya.service.DataProvider
import io.github.vehkiya.service.TextParser
import io.github.vehkiya.util.logger
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.StoredFields
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import org.apache.lucene.store.MMapDirectory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

@Service
class LuceneKtTextParser
@Autowired constructor(
    val dataProvider: DataProvider,
    private val serviceParserProperties: ServiceParserProperties
) : TextParser {

    private val luceneDir: Path = Paths.get(serviceParserProperties.indexPath, "lucene.dir")
    private val analyzer: Analyzer = StandardAnalyzer()
    private val directory: Directory = MMapDirectory(luceneDir)
    private val pattern: Pattern = Pattern.compile(serviceParserProperties.pattern)
    private val itemNameField = "itemName"
    private val log = logger<LuceneKtTextParser>()

    init {
        indexItems()
    }

    private fun indexItems() {
        val indexWriterConfig = IndexWriterConfig(analyzer)
        val indexWriter = IndexWriter(directory, indexWriterConfig)
        indexWriter.deleteAll()
        for (itemName in dataProvider.itemCache().keys) {
            val document = Document()
            document.add(TextField(itemNameField, itemName, Field.Store.YES))
            indexWriter.addDocument(document)
        }
        log.info("Indexed ${indexWriter.docStats.numDocs} items")
        indexWriter.close()
    }

    override fun messageMatchesPattern(text: String): Boolean = pattern.matcher(text).find()

    override fun parseItemsFromText(text: String): Set<Item> {
        val matcher = pattern.matcher(text)
        val buffer = mutableSetOf<String>()
        while (matcher.find()) {
            val itemNames = findAllMatchingItemNamesForTerm(matcher.group())
            buffer.addAll(itemNames)
        }
        return buffer.mapNotNull { dataProvider.findByName(it) }.toSet()
    }

    private fun findAllMatchingItemNamesForTerm(term: String): Set<String> {
        val indexReader = DirectoryReader.open(directory)
        val indexSearcher = IndexSearcher(indexReader)
        val parser = QueryParser(itemNameField, analyzer)
        parser.defaultOperator = QueryParser.Operator.AND
        val query = parser.parse("$term~10")
        val hits = indexSearcher.search(query, 10).scoreDocs
        return hits.filter { it.score > serviceParserProperties.threshold }
            .mapNotNull { indexReader.storedFields().document(it.doc).get(itemNameField) }
            .toSet()
    }
}