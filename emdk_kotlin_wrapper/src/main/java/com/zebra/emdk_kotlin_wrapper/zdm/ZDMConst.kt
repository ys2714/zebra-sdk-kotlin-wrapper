package com.zebra.emdk_kotlin_wrapper.zdm

public object ZDMConst {

    public enum class DelegationScope(val value: String) {
        SCOPE_DW_QUERY_API("delegation_scope_datawedge_query_api"),
        SCOPE_DW_RUNTIME_API("delegation_scope_datawedge_control_api"),
        SCOPE_DW_NOTIFICATION_API("delegation_scope_datawedge_notification_api"),
        SCOPE_DW_CONFIG_API("delegation_scope_datawedge_config_api")
    }

}