package myPolicy;

import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.List;

public class PolicyFactory {

    public VmAllocationPolicy make(String id, List<PowerHost> hosts) {
        switch (id) {
            case "naive":
                return new NaivePolicy(hosts);
            case "antiAffinity":
                return new AntiAffinityPolicy(hosts);
            case "mm":
                return new MaxMinPolicy(hosts);
            case "noV":
                return new NoViolationsPolicy(hosts);
            case "nibp":
                return new NIBPredictionPolicy(hosts);
            case "pamb":
                return new PAMBanlancePolicy(hosts);
            case "fcfs":
                return new FCFSPolicy(hosts);
        }
        throw new IllegalArgumentException("No such policy '" + id + "'");
    }
}