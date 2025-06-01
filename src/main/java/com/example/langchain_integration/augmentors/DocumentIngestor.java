package com.example.langchain_integration.augmentors;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.File;
import java.util.List;

@ApplicationScoped
public class DocumentIngestor {

    private static final Logger Log = Logger.getLogger(DocumentIngestor.class);

    @Inject
    EmbeddingModel embeddingModel;
    @Inject
    EmbeddingStore<TextSegment> store;

    public void ingestTaxDeductions(@Observes StartupEvent event) {
        Log.info("Ingesting documents...");

        List<Document> documents = FileSystemDocumentLoader.loadDocuments(new File("src/main/resources/documents/").toPath(), new TextDocumentParser());

        var ingestor = EmbeddingStoreIngestor.builder().embeddingStore(store).embeddingModel(embeddingModel).documentSplitter(DocumentSplitters.recursive(500, 0))  // Adjust to your API
                .build();

        ingestor.ingest(documents);
        Log.infof("Ingested %d documents.", documents.size());
    }
}