package zzhao.code.http.extend;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;

/**
 *
 * @author zzhao
 * @version 2016年4月1日
 */
public class HttpClientPoolConnectionManager extends PoolingClientConnectionManager {

    private DnsResolver dnsResolver;

    public HttpClientPoolConnectionManager(final SchemeRegistry schreg) {
        this(schreg, -1, TimeUnit.MILLISECONDS);
    }

    public HttpClientPoolConnectionManager(final SchemeRegistry schreg, final DnsResolver dnsResolver) {
        this(schreg, -1, TimeUnit.MILLISECONDS, dnsResolver);
    }

    public HttpClientPoolConnectionManager() {
        this(SchemeRegistryFactory.createDefault());
    }

    public HttpClientPoolConnectionManager(final SchemeRegistry schemeRegistry, final long timeToLive, final TimeUnit tunit) {
        this(schemeRegistry, timeToLive, tunit, new SystemDefaultDnsResolver());
    }

    public HttpClientPoolConnectionManager(final SchemeRegistry schemeRegistry, final long timeToLive, final TimeUnit tunit,
                    final DnsResolver dnsResolver) {
        super(schemeRegistry, timeToLive, tunit, dnsResolver);
        if (dnsResolver != null) {
            this.dnsResolver = dnsResolver;
        } else {
            this.dnsResolver = new SystemDefaultDnsResolver();
        }
    }

    @Override
    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new HttpClientConnectionOperator(schreg, this.dnsResolver);
    }
}
