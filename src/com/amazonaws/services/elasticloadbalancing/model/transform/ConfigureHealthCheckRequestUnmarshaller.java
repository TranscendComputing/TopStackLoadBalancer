package com.amazonaws.services.elasticloadbalancing.model.transform;

import java.util.Map;

import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class ConfigureHealthCheckRequestUnmarshaller implements
		Unmarshaller<ConfigureHealthCheckRequest, Map<String, String[]>> {
	// private static Logger logger = Appctx
	// .getLogger(CreateAppCookieStickinessPolicyRequestUnmarshaller.class
	// .getName());

	private static ConfigureHealthCheckRequestUnmarshaller instance;

	public static ConfigureHealthCheckRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new ConfigureHealthCheckRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public ConfigureHealthCheckRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		try {
			final ConfigureHealthCheckRequest req = new ConfigureHealthCheckRequest();
			req.setLoadBalancerName(in.get("LoadBalancerName")[0]);
			final HealthCheck hc = new HealthCheck();
			hc.setHealthyThreshold(QueryUtil.requiredInt(in,
					"HealthCheck.HealthyThreshold"));
			hc.setInterval(QueryUtil.requiredInt(in, "HealthCheck.Interval"));
			hc.setTarget(QueryUtil.requiredString(in, "HealthCheck.Target"));
			hc.setTimeout(QueryUtil.requiredInt(in, "HealthCheck.Timeout"));
			hc.setUnhealthyThreshold(QueryUtil.requiredInt(in,
					"HealthCheck.UnhealthyThreshold"));
			req.setHealthCheck(hc);
			if (hc.getHealthyThreshold() <= 0 || hc.getInterval() <= 0
					|| hc.getTimeout() <= 0 || hc.getUnhealthyThreshold() <= 0) {
				throw LoadBalancerQueryFaults.validationError();
			}
			return req;
		} catch (final ErrorResponse err) {
			throw err;
		} catch (final Exception e) {
			throw LoadBalancerQueryFaults.validationError();
		}
	}
}
