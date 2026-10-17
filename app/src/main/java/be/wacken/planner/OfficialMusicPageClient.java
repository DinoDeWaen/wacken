package be.wacken.planner;

import android.text.Html;
import android.text.Spanned;
import android.text.style.URLSpan;
import be.wacken.planner.application.MusicLinkEvidence;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Bounded public HTTPS retrieval. No cookies, credentials, scripts or automatic redirects. */
final class OfficialMusicPageClient {
    interface PageLoader {MusicLinkEvidence.Page read(String url)throws IOException;}
    interface Resolver {InetAddress[] addresses(String host)throws IOException;}
    interface Connections {HttpURLConnection open(URL url)throws IOException;}
    interface Links {List<String> parse(String html,URI base);}
    private final Resolver resolver;
    private final Connections connections;
    private final Links links;
    private final int byteLimit,redirectLimit;

    OfficialMusicPageClient(){
        this(InetAddress::getAllByName,url->(HttpURLConnection)url.openConnection(Proxy.NO_PROXY),
                OfficialMusicPageClient::anchorLinks,512*1024,3);
    }
    OfficialMusicPageClient(Resolver resolver,Connections connections,Links links,int byteLimit,int redirectLimit){
        this.resolver=resolver;this.connections=connections;this.links=links;this.byteLimit=byteLimit;this.redirectLimit=redirectLimit;
    }

    MusicLinkEvidence.Page read(String sourceUrl)throws IOException {
        URI source=uri(sourceUrl),current=source;
        for(int redirects=0;redirects<=redirectLimit;redirects++){
            checkInterrupted();validatePublic(current);
            HttpURLConnection connection=connections.open(current.toURL());
            try {
                connection.setInstanceFollowRedirects(false);
                connection.setConnectTimeout(5000);connection.setReadTimeout(5000);
                connection.setRequestProperty("Accept","text/html,application/xhtml+xml");
                connection.setRequestProperty("User-Agent","WackenPlanner music-link verification");
                int status=connection.getResponseCode();
                if(status>=300&&status<400){
                    String destination=connection.getHeaderField("Location");
                    if(destination==null)throw new IOException("Official site redirect has no destination.");
                    current=uri(current.resolve(destination).toString());continue;
                }
                if(status!=200)throw new IOException("Official site evidence unavailable (HTTP "+status+").");
                String type=connection.getContentType();
                if(type!=null&&!type.toLowerCase(Locale.ROOT).startsWith("text/html")
                        &&!type.toLowerCase(Locale.ROOT).startsWith("application/xhtml+xml"))
                    throw new IOException("Official site did not return an HTML page.");
                try(InputStream input=connection.getInputStream();ByteArrayOutputStream output=new ByteArrayOutputStream()){
                    byte[] buffer=new byte[8192];int count;
                    while((count=input.read(buffer))!=-1){
                        checkInterrupted();
                        if(output.size()+count>byteLimit)throw new IOException("Official site evidence exceeds size limit.");
                        output.write(buffer,0,count);
                    }
                    return new MusicLinkEvidence.Page(source.toString(),
                            links.parse(output.toString(StandardCharsets.UTF_8.name()),current));
                }
            } finally {connection.disconnect();}
        }
        throw new IOException("Official site redirect limit exceeded.");
    }
    private URI uri(String value)throws IOException {
        try {
            URI uri=URI.create(value).normalize();
            // Upgrade a declared HTTP homepage; never send an insecure request.
            if("http".equalsIgnoreCase(uri.getScheme())&&(uri.getPort()==-1||uri.getPort()==80))
                uri=new URI("https",uri.getUserInfo(),uri.getHost(),-1,uri.getPath(),uri.getQuery(),null);
            if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null
                    ||(uri.getPort()!=-1&&uri.getPort()!=443))throw new IOException("Official site requires a public HTTPS URL.");
            return uri;
        }catch(IllegalArgumentException|URISyntaxException error){throw new IOException("Invalid official site URL.",error);}
    }
    private void validatePublic(URI uri)throws IOException {
        String host=uri.getHost().toLowerCase(Locale.ROOT);
        if(host.equals("localhost")||host.endsWith(".localhost")||host.endsWith(".local"))
            throw new IOException("Local official-site destinations are not allowed.");
        InetAddress[] addresses=resolver.addresses(host);
        if(addresses.length==0)throw new IOException("Official site address could not be resolved.");
        for(InetAddress address:addresses){
            byte[] bytes=address.getAddress();
            boolean privateV4=bytes.length==4&&((bytes[0]&255)==0||(bytes[0]&255)==127
                    ||((bytes[0]&255)==100&&(bytes[1]&255)>=64&&(bytes[1]&255)<=127)
                    ||(bytes[0]&255)>=224);
            boolean privateV6=bytes.length==16&&((bytes[0]&0xfe)==0xfc);
            if(address.isAnyLocalAddress()||address.isLoopbackAddress()||address.isLinkLocalAddress()
                    ||address.isSiteLocalAddress()||address.isMulticastAddress()||privateV4||privateV6)
                throw new IOException("Private official-site destinations are not allowed.");
        }
    }
    private void checkInterrupted()throws InterruptedIOException {
        if(Thread.currentThread().isInterrupted())throw new InterruptedIOException("Music-link lookup cancelled.");
    }
    @SuppressWarnings("deprecation")
    static List<String> anchorLinks(String html,URI base){
        Spanned text=Html.fromHtml(html);
        List<String> links=new ArrayList<>();
        for(URLSpan span:text.getSpans(0,text.length(),URLSpan.class)){
            try {links.add(base.resolve(span.getURL()).toString());}catch(IllegalArgumentException ignored){}
        }
        return List.copyOf(links);
    }
}

