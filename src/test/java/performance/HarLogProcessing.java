package performance;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarPage;
import net.lightbody.bmp.core.har.HarPageTimings;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertTrue;

public class HarLogProcessing {

    public void harToElasticSearch(String testName, HarLog har) throws IOException {

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .defaultCredentials("elastic", "changeme")
                .multiThreaded(true)
                .build());
        JestClient client = factory.getObject();

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());

        String indexName = timeStamp + "-detailed-perf-" + testName;
        String documentType = "HarLog";

//Create Index and set settings and mappings
        client.execute(new CreateIndex.Builder(indexName).build());


//Add documents
        Map<String, Object> source = new LinkedHashMap<String, Object>();
        source.put("date", LocalDateTime.now().toString());
        Map<String, Object> pages = new LinkedHashMap<String, Object>();
        Map<String, Object> entries = new LinkedHashMap<String, Object>();

        int pageCounter = 0;
        for (HarPage page : har.getPages()) {
            Map<String, Object> p = new LinkedHashMap<String, Object>();
            p.put("page.id", page.getId());
            p.put("page.comment", page.getComment());
            p.put("page.start.datetime", page.getStartedDateTime());
            p.put("page.title", page.getTitle());
            HarPageTimings time = page.getPageTimings();
            p.put("page.time.comment", time.getComment());
            p.put("page.time.contentload", time.getOnContentLoad());
            p.put("page.time.onload", time.getOnLoad());

            source.put(page.getId(), p);

            p.put("date", DateTime.now().toDateTime(DateTimeZone.UTC));
            p.put("test", testName);

            JestResult r = client.execute(new Index.Builder(p).index(timeStamp + "-page-performance").type("page").build());

            pageCounter++;
        }

//        int entryCounter = 0;
//        for (HarEntry entry : har.getEntries()) {
//            Map<String, Object> p = new LinkedHashMap<String, Object>();
//            p.put("entry.comment", entry.getComment());
//            p.put("entry.start.datetime", entry.getStartedDateTime());
//            p.put("entry.connection", entry.getConnection());
//            p.put("entry.pageref", entry.getPageref());
//            p.put("entry.server.ip", entry.getServerIPAddress());
//            HarTimings time = entry.getTimings();
//            p.put("entry.time.comment", time.getComment());
//            p.put("entry.time.wait", time.getWait());
//            p.put("entry.time.blocked", time.getBlocked());
//            p.put("entry.time.connect", time.getConnect());
//            p.put("entry.time.dnc", time.getDns());
//            p.put("entry.time.recieve", time.getReceive());
//            p.put("entry.time.send", time.getSend());
//            p.put("entry.time.ssl", time.getSsl());
//            HarResponse response = entry.getResponse();
//            p.put("response.size.body", response.getBodySize());
//            p.put("response.comment", response.getComment());
//            p.put("response.content", response.getContent());
//            p.put("response.cookies", response.getCookies());
//            p.put("response.error", response.getError());
//            p.put("response.headers", response.getHeaders());
//            HarRequest request = entry.getRequest();
//            p.put("request.size.body", request.getBodySize());
//            p.put("request.comment", request.getComment());
//            p.put("request.cookies", request.getCookies());
//            p.put("request.headers", request.getHeaders());
//            p.put("request.size.headers", request.getHeadersSize());
//            p.put("request.http.version", request.getHttpVersion());
//            p.put("request.method", request.getMethod());
//            p.put("request.post.data", request.getPostData());
//            p.put("request.query", request.getQueryString());
//            p.put("request.url", request.getUrl());
//            HarCache cache = entry.getCache();
//            p.put("cache.request.after", cache.getAfterRequest());
//            p.put("cache.request.before", cache.getBeforeRequest());
//
//            entryCounter++;
//            entries.put(Integer.toString(entryCounter), p);
//
//        }

        source.put("Pages", pages);
        source.put("Entries", entries);


        Index index = new Index.Builder(source).index(indexName).type(documentType).build();

        JestResult result = client.execute(index);
        //assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    public void processHarLog2ES(HarLog har, String testName) throws IOException {
        // on startup
        //Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "localtestsearch").build();

        // Construct a new Jest client according to configuration via factory
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .defaultCredentials("elastic", "changeme")
                .multiThreaded(true)
                .build());
        JestClient client = factory.getObject();

        //Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("http://es.aws.lazycoder.io", 9300));
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());

        String indexName = String.format("%s-perf-%s", timeStamp, testName).toLowerCase();
        String documentType = "HarLog";

//Create Index and set settings and mappings
        client.execute(new CreateIndex.Builder(indexName).build());


//Add documents
        Map<String, Object> source = new LinkedHashMap<String, Object>();
       // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSSSSS Z");
        source.put("Date", LocalDateTime.now().toString());
        source.put("TestName", testName);
        source.put("log", har);

        Index index = new Index.Builder(source).index(indexName).type(documentType).build();

        JestResult result = client.execute(index);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
    public void processHarLog2Tick(HarLog har, String testName) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");

        if (!influxDB.databaseExists("pages"))
        {
            influxDB.createDatabase("pages");
        }

        BatchPoints bp = BatchPoints.database("pages")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        for (HarPage p : har.getPages()) {
            bp.point(Point.measurement("page")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .fields(harPageToMap(p.getPageTimings()))
                    .tag("testname", testName)
                    .tag("title", p.getTitle())
                    .tag("id", p.getId())
                    .build());
        }
        influxDB.write(bp);

        if (!influxDB.databaseExists("entries")) {
            influxDB.createDatabase("entries");

        }

        BatchPoints bpe = BatchPoints.database("entries")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        for (HarEntry e : har.getEntries()) {
            bpe.point(
                    Point.measurement(
                            e.getRequest().getUrl())
                    .time(e.getTime(), TimeUnit.MINUTES)
                    .fields(harEntryToMap(e))
                    .tag("testname", testName)
                    .tag("page_ref", e.getPageref())
                    .build());
        }

        influxDB.write(bpe);
    }

    public long longOrZero(long l) {
        try {
            return l;
        } catch (NullPointerException e) {
            return 0L;
        }
    }

    public Map<String, Object> harPageToMap(HarPageTimings hpt){
        Map<String, Object> toReturn = new HashMap<>();

        long ocl = 0l;
        try {
            ocl = hpt.getOnContentLoad();
        }catch (NullPointerException e)
        { }
        long ol = 0l;
        try {
            ol = hpt.getOnLoad();
        }
        catch (NullPointerException e){}

        toReturn.put("onContentLoad", ocl);
        toReturn.put("onLoad", ol);
        return toReturn;
    }
    public Map<String, Object> harEntryToMap(HarEntry he){
        Map<String, Object> toReturn = new HashMap<>();


        long receive = 0l;
        try {
            receive = he.getTimings().getReceive();
        }catch (NullPointerException e)
        { }

        long blocked = 0l;
        try {
            blocked = he.getTimings().getBlocked();
        }catch (NullPointerException e)
        { }

        long connect = 0l;
        try {
            connect = he.getTimings().getConnect();
        }catch (NullPointerException e)
        { }

        long dns = 0l;
        try {
            dns = he.getTimings().getDns();
        }catch (NullPointerException e)
        { }
        long send = 0l;
        try {
            send = he.getTimings().getSend();
        }catch (NullPointerException e)
        { }

        long ssl = 0l;
        try {
            ssl = he.getTimings().getSsl();
        }catch (NullPointerException e)
        { }

        long wait = 0l;
        try {
            wait = he.getTimings().getWait();
        }catch (NullPointerException e)
        { }

        toReturn.put("receive", receive);
        toReturn.put("blocked", blocked);
        toReturn.put("connect", connect);
        toReturn.put("dns", dns);
        toReturn.put("send", send);
        toReturn.put("ssl", ssl);
        toReturn.put("wait", wait);
        return toReturn;
    }
}
