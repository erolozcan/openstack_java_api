package unlenen.cloud.openstack.be.modules.network.result;

import java.util.ArrayList;

import unlenen.cloud.openstack.be.model.result.OpenStackResult;
import unlenen.cloud.openstack.be.modules.network.models.Subnet;

public class SubnetResult implements OpenStackResult {
    public ArrayList<Subnet> subnets;
}
