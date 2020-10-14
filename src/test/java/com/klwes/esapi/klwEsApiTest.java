package com.klwes.esapi;

import com.alibaba.fastjson.JSON;
import com.klwes.esapi.pojo.User;
import net.minidev.json.JSONArray;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @description:
 * @author: klw
 * @time: 2020-10-12 9:50
 */
@SpringBootTest
public class klwEsApiTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testCreateIndex() throws IOException {
        //创建索引
        CreateIndexRequest indexRequest = new CreateIndexRequest("klw_index");
        //客户端执行请求
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //获取索引
    @Test
    void getIndex() throws IOException {
        GetIndexRequest indexRequest = new GetIndexRequest("klw_index");
        boolean exists = restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    //删除索引

    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest("klw_index");
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(acknowledgedResponse);

    }

    @Test
    void addDocuments() throws IOException {
        //创建对象
        User user = new User("康刘文", 23, "睿智");
        //创建请求
        IndexRequest request = new IndexRequest("klw_index");
        request.id("1");
        request.timeout(TimeValue.timeValueDays(1));

        //将我们数据放入请求
        request.source(JSON.toJSONString(user), XContentType.JSON);

        //客户端发送请求
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.status());//命令放回的状态
    }

    //判断文档是否存在
    @Test
    void testIsExist() throws IOException {
        GetRequest klwIndexRequest = new GetRequest("klw_index", "1");
        boolean exists = restHighLevelClient.exists(klwIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
        GetResponse response = restHighLevelClient.get(klwIndexRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());//打印文档内容

    }

    //更新文档信息
    @Test
    void testUpdate() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("klw_index", "1");
        User u = new User("酷酷酷", 23, "耶耶耶耶");
        updateRequest.doc(JSON.toJSONString(u), XContentType.JSON);
        UpdateResponse updateRe = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateRe.status());
    }

    //删除文档
    @Test
    void testDelete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("klw_index", "1");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }

    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.add(new User("康", 22, "聪明"));
        userArrayList.add(new User("刘", 21, "能干"));
        userArrayList.add(new User("文", 20, "厉害"));
        userArrayList.add(new User("干", 19, "刺激"));

        for (int i = 0; i < userArrayList.size(); i++) {
            bulkRequest.add(new IndexRequest("klw_index")

                    .source(JSON.toJSONString(userArrayList.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println(bulk.status());
        System.out.println(bulk.hasFailures());//是否失败

    }

    //查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("klw_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "康");
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.from();
        searchSourceBuilder.size();

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
        ;
    }
}
