package com.example.query.service;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BloomFilterFactory {
    public static BloomFilter<String> createLogBloomFilter(int expectedInsertions, double fpp) {
        // Bloom filter uses SHA-256 hash strings
        return BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), expectedInsertions, fpp);
    }
}
