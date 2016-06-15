package zzhao.code.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.conn.DnsResolver;
import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

/**
 * 支持自定义hosts的dns解析器
 * @author zzhao
 * @version 2016年6月15日
 */
class SpecialDNSResolver implements DnsResolver {

    private static final Logger logger = Logger.getLogger(SpecialDNSResolver.class);

    private Map<String, InetAddress[]> hostMaps = Maps.newHashMap();

    public SpecialDNSResolver(Map<String, String> hosts) {
        for (Entry<String, String> entry : hosts.entrySet()) {
            InetAddress[] address;
            try {
                address = new InetAddress[] {InetAddress.getByName(entry.getValue())};
                hostMaps.put(entry.getKey(), address);
            } catch (UnknownHostException e) {
                logger.error("can't get address for " + entry.getValue(), e);
            }
        }
    }

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        return hostMaps.containsKey(host) ? hostMaps.get(host) : InetAddress.getAllByName(host);
    }
}
