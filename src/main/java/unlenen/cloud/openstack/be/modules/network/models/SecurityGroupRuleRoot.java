package unlenen.cloud.openstack.be.modules.network.models;

import com.fasterxml.jackson.annotation.JsonAlias;

import unlenen.cloud.openstack.be.model.request.OpenStackRequest;

public class SecurityGroupRuleRoot implements OpenStackRequest {
    @JsonAlias("security_group_rule")
    public SecurityGroupRule security_group_rule;
}
