package com.baizhi.test;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class TestLink {
    @Test
    public void testConteection() throws UnknownHostException {
        TransportClient transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.153.129"), 9300));
        SearchResponse searchResponse = transportClient.prepareSearch("dangdang").setTypes("book").setQuery(QueryBuilders.matchAllQuery()).setFetchSource("*","age").get();
        SearchHits hits = searchResponse.getHits();
        System.out.println("符合条件的记录数: "+hits.totalHits);
        for (SearchHit hit : hits) {
            System.out.print("当前索引的分数: "+hit.getScore());
            System.out.print(", q对应结果:=====>"+hit.getSourceAsString());
            System.out.println(",q 指定字段结果:"+hit.getSourceAsMap().get("name"));
            System.out.println("=================================================");
            System.out.println("dddddfd");
    }
    }
    @Test
    public void testMatchAllQuery() throws UnknownHostException {
        TransportClient transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.153.129"), 9300));
        SearchResponse searchResponse = transportClient.prepareSearch("dangdang").setTypes("book").setQuery(QueryBuilders.matchAllQuery()).get();
        SearchHits hits = searchResponse.getHits();
        System.out.println("1符合条件的记录数: "+hits.totalHits);
        for (SearchHit hit : hits) {
            System.out.print("当前索引的分数: "+hit.getScore());
            System.out.print(", 对应结果:=====>"+hit.getSourceAsString());
            System.out.println(", 指定字段结果:"+hit.getSourceAsMap().get("name"));
            System.out.println("=================================================");
        }
    }
    //创建索引并且创建类型指定映射
    @Test
    public void testCreateIndexAndTypeAndMapping() throws IOException, ExecutionException, InterruptedException {
        TransportClient transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.153.129"), 9300));
        System.out.println("=======创建索引=======");
        CreateIndexResponse indexResponse = transportClient.admin().indices().prepareCreate("dangdang").execute().get();
        System.out.println(indexResponse.index());

        System.out.println("====第一次push");
        System.out.println("=====2次");
        System.out.println("=======创建类型指定映射=======");
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder();
        mappingBuilder.startObject()
                .startObject("properties")
                .startObject("name")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("age")
                .field("type", "integer")
                .endObject()
                .startObject("sex")
                .field("type", "keyword")
                .endObject()
                .startObject("content")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .endObject()
                .endObject();

        PutMappingRequest putMappingRequest = new PutMappingRequest("dangdang").type("book").source(mappingBuilder);
        transportClient.admin().indices().putMapping(putMappingRequest).get();
    }
    //添加一条记录
    @Test
    public void testCreate() throws IOException {
        TransportClient transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.153.129"), 9300));
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                .field("name","中国人")
                .field("age",23)
                .field("sex","男")
                .field("content","他是一个中国人,这个中国人怎么样,挺好的").endObject();
        IndexResponse indexResponse = transportClient.prepareIndex("dangdang", "book").setSource(xContentBuilder).get();
        System.out.println(indexResponse.status());
    }
}
