package zzhao.code.http.extend;

import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 *
 * @author zzhao
 * @version 2016年4月1日
 */
public class HttpClientPoolConnectionManager extends PoolingClientConnectionManager {

    @Override
    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        // 这里没用PoolingClientConnectionManager的dnsResolver，可能会造成隐患
        return new HttpClientConnectionOperator(schreg);
    }
}
