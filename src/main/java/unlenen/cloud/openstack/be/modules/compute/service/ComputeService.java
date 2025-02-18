package unlenen.cloud.openstack.be.modules.compute.service;

import java.lang.reflect.Field;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import unlenen.cloud.openstack.be.config.OpenStackConfig;
import unlenen.cloud.openstack.be.constant.Call;
import unlenen.cloud.openstack.be.constant.OpenStackHeader;
import unlenen.cloud.openstack.be.constant.OpenStackModule;
import unlenen.cloud.openstack.be.constant.Parameter;
import unlenen.cloud.openstack.be.constant.ParameterType;
import unlenen.cloud.openstack.be.modules.compute.models.Quota;
import unlenen.cloud.openstack.be.modules.compute.result.FlavorCreateResult;
import unlenen.cloud.openstack.be.modules.compute.result.FlavorResult;
import unlenen.cloud.openstack.be.modules.compute.result.KeypairCreateResult;
import unlenen.cloud.openstack.be.modules.compute.result.KeypairResult;
import unlenen.cloud.openstack.be.modules.compute.result.QuotaResult;
import unlenen.cloud.openstack.be.modules.compute.result.ServerResult;
import unlenen.cloud.openstack.be.service.CommonService;

/**
 *
 * @author Nebi
 */
@Service
public class ComputeService extends CommonService {

    @Autowired
    OpenStackConfig openStackConfig;

    @Call(type = HttpMethod.GET, url = "/flavors/detail", statusCode = HttpStatus.OK, openstackResult = FlavorResult.class)
    public FlavorResult getFlavors(String token) throws Exception {
        return (FlavorResult) callWithResult(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[0]);
    }

    @Call(type = HttpMethod.POST, url = "/flavors", bodyFile = "payloads/compute/flavor_create.json", statusCode = HttpStatus.OK, openstackResult = FlavorCreateResult.class)
    public FlavorCreateResult createFlavor(String token, String name, int vcpus, int ram, int disk) throws Exception {
        return (FlavorCreateResult) callWithResult(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("NAME", name, ParameterType.JSON),
                        new Parameter("RAM", ram + "", ParameterType.JSON),
                        new Parameter("VCPUS", vcpus + "", ParameterType.JSON),
                        new Parameter("DISK", disk + "", ParameterType.JSON)
                });
    }

    @Call(type = HttpMethod.DELETE, url = "/flavors/{flavor_id}", statusCode = HttpStatus.ACCEPTED)
    public void deleteFlavor(String token, String flavorId) throws Exception {
        call(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("flavor_id", flavorId, ParameterType.URI)
                });
    }

    @Call(type = HttpMethod.GET, url = "/servers/detail", statusCode = HttpStatus.OK, openstackResult = ServerResult.class)
    public ServerResult getServers(String token, String projectId) throws Exception {
        return (ServerResult) callWithResult(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("project_id", projectId, ParameterType.REQUEST),
                        new Parameter("all_tenants", "true", ParameterType.REQUEST)
                });

    }

    @Call(type = HttpMethod.GET, url = "/os-keypairs", statusCode = HttpStatus.OK, openstackResult = KeypairResult.class)
    public KeypairResult getKeypairs(String token, String userId) throws Exception {
        return (KeypairResult) callWithResult(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("user_id", userId, ParameterType.REQUEST),
                });
    }

    @Call(type = HttpMethod.POST, url = "/os-keypairs", bodyFile = "payloads/compute/keypair_create.json", statusCode = HttpStatus.OK, openstackResult = KeypairCreateResult.class)
    public KeypairCreateResult createKeypair(String token, String name, String public_key) throws Exception {
        return (KeypairCreateResult) callWithResult(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("NAME", name, ParameterType.JSON),
                        new Parameter("PUBLIC_KEY", public_key, ParameterType.JSON),
                });
    }

    @Call(type = HttpMethod.DELETE, url = "/os-keypairs/{keypair_name}", statusCode = HttpStatus.ACCEPTED)
    public void deleteKeypair(String token, String keypairName) throws Exception {
        call(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("keypair_name", keypairName, ParameterType.URI)
                });
    }

    @Call(type = HttpMethod.GET, url = "/os-quota-sets/{tenant_id}", statusCode = HttpStatus.OK, openstackResult = QuotaResult.class)
    public QuotaResult getQuota(String token, String tenant_id) throws Exception {
        return (QuotaResult) callWithResult(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("tenant_id", tenant_id, ParameterType.URI),
                });
    }

    @Call(type = HttpMethod.PUT, url = "/os-quota-sets/{tenant_id}", statusCode = HttpStatus.OK, openstackResult = QuotaResult.class)
    public QuotaResult updateQuota(String token, String tenant_id, Quota quota) throws Exception {
        JSONObject root = new JSONObject();
        JSONObject quota_set = new JSONObject();
        root.put("quota_set", quota_set);

        for (Field field : quota.getClass().getDeclaredFields()) {
            String name = field.getName();
            Object val = field.get(quota);
            if (val != null)
                quota_set.put(name, val);
        }

        return (QuotaResult) callWithResult(getServiceURL(token, OpenStackModule.compute),
                new Parameter[] {
                        new Parameter(OpenStackHeader.TOKEN.getKey(), token)
                },
                new Parameter[] {
                        new Parameter("tenant_id", tenant_id, ParameterType.URI),
                },
                root.toString(),
                3);
    }
}
