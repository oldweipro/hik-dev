package com.oldwei.hikdev.structure;

import com.oldwei.hikdev.constant.HikConstant;
import com.sun.jna.Structure;

/**
 * @author oldwei
 * @date 2021-5-18 12:29
 */
public class NET_VCA_RULE_INFO extends Structure {
    public byte byRuleID;
    public byte byRes;
    public short wEventTypeEx;
    public byte[] byRuleName= new byte[HikConstant.NAME_LEN];
    public int dwEventType;
    public NET_VCA_EVENT_UNION uEventParam;
    @Override
    public void read() {
        super.read();
        switch (wEventTypeEx) {
            case 1:
                uEventParam.setType(NET_VCA_TRAVERSE_PLANE.class);
                break;
            case 2:
            case 3:
                uEventParam.setType(NET_VCA_AREA.class);
                break;
            default:
                break;
        }
        uEventParam.read();
    }
    @Override
    public void write() {
        super.write();
        uEventParam.write();
    }
}
