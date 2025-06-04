package com.example.langchain_integration.augmentors;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.function.Supplier;

@ApplicationScoped
public class TranslateResponseAugmenter implements Supplier<RetrievalAugmentor> {

    private final EmbeddingStoreContentRetriever retriever;

    @Inject
    public TranslateResponseAugmenter(EmbeddingStore<TextSegment> store, EmbeddingModel model) {
        this.retriever = EmbeddingStoreContentRetriever.builder().embeddingModel(model).embeddingStore(store).maxResults(20).build();
    }

    @Override
    public RetrievalAugmentor get() {
        return DefaultRetrievalAugmentor.builder().contentRetriever(retriever).build();
    }
}