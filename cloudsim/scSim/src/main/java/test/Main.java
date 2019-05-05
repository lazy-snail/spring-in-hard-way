// https://github.com/mourjo/VMScheduling

package test;

import myPolicy.Constants;
import myPolicy.VmAllocationPolicyFactory;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.util.MathUtil;
import utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

import static examples.power.Helper.*;
import static utils.Helper.getSlaTimePerActiveHost;
import static utils.Helper.getTimesBeforeHostShutdown;

/**
 * Entry point of the project.
 * It just initialises the simulator, create the right scheduling algorithm and runs the simulation.
 * <p>
 * Nothing to edit on your side.
 *
 * @author Fabien Hermenier
 */
public class Main {

    public static String WORKLOAD = "workload/planetlab";

    public static boolean doLog = true;

    private static VmAllocationPolicyFactory policies = new VmAllocationPolicyFactory();

    private static Observers observers = new Observers();

    private static Revenue simulateDay(String d, String impl) throws Exception {
        File input = new File(WORKLOAD + "/" + d);
        if (!input.isDirectory()) {
            quit("no workload for day " + d);
        }
        //Initialise the simulator
        CloudSim.init(1, Calendar.getInstance(), false);

        //The broker is the client interface where to submit the VMs
        DatacenterBroker broker = new PowerDatacenterBroker("Broker");

        //The applications to run, on their Vms
        List<Cloudlet> cloudlets = Helper.createCloudletListPlanetLab(broker.getId(), input.getPath());
        List<Vm> vms = Helper.createVmList(broker.getId(), cloudlets);
        broker.submitVmList(vms);
        broker.submitCloudletList(cloudlets);

        //800 hosts
        List<PowerHost> hosts = Helper.createHostList(800);

        //The scheduling algorithm
        VmAllocationPolicy policy = policies.make(impl, hosts);

        //the datacenter
        PowerDatacenter datacenter = Helper.createDatacenter("Datacenter", hosts, policy);

        prepareLogging(d);
        CloudSim.terminateSimulation(Constants.SIMULATION_LIMIT);

        //Here you can insert your observers
        PeakPowerObserver peakPowerObserver = new PeakPowerObserver(hosts);
        observers.build(hosts);

        double x = CloudSim.startSimulation();

        List<Cloudlet> newList = broker.getCloudletReceivedList();

        Log.printLine("Received " + newList.size() + " cloudlets");

        System.out.println();
        System.out.println("___________________________________________________________________");
        System.out.println("SlaTimePerActiveHost: " + getSlaTimePerActiveHost(datacenter.getHostList()));
        System.out.println("SlaTimePerHost: " + Helper.getSlaTimePerHost(datacenter.getHostList()));
        System.out.println("Power: " + datacenter.getPower());
        System.out.println("MigrationCount: " + datacenter.getMigrationCount());
//        System.out.println("TimesBeforeHostShutdown: " + Helper.getTimesBeforeHostShutdown(datacenter.getHostList()));
        System.out.println();
        System.out.println("___________________________________________________________________");

        printResults(datacenter, vms, x, "test", true, "test");

        CloudSim.stopSimulation();
        Log.printLine("Finished");
        return new Revenue(peakPowerObserver, datacenter);
    }

    public static void main(String[] args) {

        // 静态方法中获取当前执行路径2
        String currPath = Class.class.getClass().getResource("/").getPath();
        WORKLOAD = currPath + WORKLOAD;

        CumulatedRevenue revenues = new CumulatedRevenue();
        if (args.length < 1) {
            quit("Usage: Main --scheduler [day]");
        }
        String policy = args[0].substring(2);  //get rid of the leading "--"

        if (args.length == 1 || args[1].equals("all")) {
            //we process every day
            File input = new File(WORKLOAD);
            if (!input.isDirectory()) {
                quit(WORKLOAD + " is not a folder");
            }

            File[] content = input.listFiles();
            Arrays.sort(content);
            for (File f : content) {
                try {
                    System.out.println("Day " + f.getName());
                    Revenue r = simulateDay(f.getName(), policy);
                    System.out.println(r);
                    revenues.add(r);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            //a single day
            try {
                System.out.println("Day " + args[1]);
                Revenue r = simulateDay(args[1], policy);
                System.out.println(r);
                revenues.add(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.err.println("hop");
        System.out.println(revenues);
    }

    private static void prepareLogging(String d) throws FileNotFoundException {
        if (!doLog) {
            Log.disable();
            return;
        }
        File output = new File("logs/" + d);
        if (!output.exists()) {
            if (!output.mkdirs()) {
                quit("Unable to create log folder '" + output + "'");
            }
        }
        Log.setOutput(new FileOutputStream("logs/" + d + "/log.txt"));
    }

    public static void quit(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    public static void printResults(PowerDatacenter datacenter, List<Vm> vms,
                                    double lastClock, String experimentName, boolean outputInCsv, String outputFolder) {

        Log.enable();                                                           //启用输出
        List<Host> hosts = datacenter.getHostList();                            //获取主机列表

        int numberOfHosts = hosts.size();                                       //获取主机列表中主机的数量
        int numberOfVms = vms.size();                                           //获取虚拟机列表中虚拟机的数量

        double totalSimulationTime = lastClock;                                 //将最后一个时钟时间赋值给totalSimulationTime(总共仿真时间)
        double energy = datacenter.getPower() / (3600 * 1000);                  //获得数据中心能耗值
        int numberOfMigrations = datacenter.getMigrationCount();                //获得迁移计数


        Map<String, Double> slaMetrics = Helper.getSlaMetrics(vms);                    //获取SLA指标（参数为虚拟机列表），返回Map集合类型的SLA指标

        double slaOverall = slaMetrics.get("overall");                                          //获得键“overall”所对应的值并赋值给slaOverall
        double slaAverage = slaMetrics.get("average");                                          //获得键“average”所对应的值并赋值给slaAverage
        double slaDegradationDueToMigration = slaMetrics.get("underallocated_migration");       //获得键“underallocated_migration”所对应的值并赋值给slaDegradationDueToMigration
//        double slaTimePerVmWithMigration = slaMetrics.get("sla_time_per_vm_with_migration");
//        double slaTimePerVmWithoutMigration = slaMetrics.get("sla_time_per_vm_without_migration");
//        System.out.println("slaTimePerVmWithMigration: " + slaTimePerVmWithMigration);
//        System.out.println("slaTimePerVmWithoutMigration: " + slaTimePerVmWithoutMigration);

        // double slaTimePerHost = getSlaTimePerHost(hosts);
        double slaTimePerActiveHost = getSlaTimePerActiveHost(hosts);                        //获取每个活跃主机的SLA（服务等级协议）指标

        double sla = slaTimePerActiveHost * slaDegradationDueToMigration;                    //获得每个活跃主机的SLA指标*因迁移引起的SLA下降的SLA指标，并赋值给sla

        List<Double> timeBeforeHostShutdown = getTimesBeforeHostShutdown(hosts);              //得到主机关机之前的时间列表，并赋值给timeBeforeHostShutdown

        int numberOfHostShutdowns = timeBeforeHostShutdown.size();                  //得到主机关机之前的时间列表的长度，并赋值给numberOfHostShutdowns，即主机关机数量

        double meanTimeBeforeHostShutdown = Double.NaN;                  //将double类型的NaN值的常量赋值给meanTimeBeforeHostShutdown
        double stDevTimeBeforeHostShutdown = Double.NaN;                 //将double类型的NaN值的常量赋值给stDevTimeBeforeHostShutdown

        if (!timeBeforeHostShutdown.isEmpty()) {                                    //判断主机关机之前的时间列表是否为空，即判断主机是否有开机
            meanTimeBeforeHostShutdown = MathUtil.mean(timeBeforeHostShutdown);     //若主机有开机，将主机关机之前的时间列表（timeBeforeHostShutdown）求平均值，并将平均值赋值给meanTimeBeforeHostShutdown
            //mean（）：在MathUtil类中定义，用来从数字列表中获取平均值

            stDevTimeBeforeHostShutdown = MathUtil.stDev(timeBeforeHostShutdown);   //若主机有开机，将主机关机之前的时间列表（timeBeforeHostShutdown）求标准偏差，并将平均值赋值给stDevTimeBeforeHostShutdown
            //stDev（）：在MathUtil类中定义，用来从数字列表中获取标准偏差
        }

        List<Double> timeBeforeVmMigration = getTimesBeforeVmMigration(vms);        //得到虚拟机迁移之前的时间列表，并赋值给timeBeforeVmMigration
        double meanTimeBeforeVmMigration = Double.NaN;                              //将double类型的NaN值的常量赋值给meanTimeBeforeVmMigration
        double stDevTimeBeforeVmMigration = Double.NaN;                             //将double类型的NaN值的常量赋值给stDevTimeBeforeVmMigration

        if (!timeBeforeVmMigration.isEmpty()) {                                      //判断虚拟机迁移之前的时间列表是否为空，即判断虚拟机是否有未迁移时间
            meanTimeBeforeVmMigration = MathUtil.mean(timeBeforeVmMigration);        //若虚拟机有未迁移时间， 即虚拟机并非一直在迁移，将虚拟机未迁移时间（timeBeforeVmMigration）求平均值，并将平均值赋值给meanTimeBeforeVmMigration

            stDevTimeBeforeVmMigration = MathUtil.stDev(timeBeforeVmMigration);      //若虚拟机有未迁移时间，即虚拟机并非一直在迁移，将虚拟机未迁移时间（timeBeforeVmMigration） 求标准偏差，并将平均值赋值给stDevTimeBeforeVmMigration
        }


        if (outputInCsv) {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            File folder1 = new File(outputFolder + "/stats");
            if (!folder1.exists()) {
                folder1.mkdir();
            }
            File folder2 = new File(outputFolder + "/time_before_host_shutdown");
            if (!folder2.exists()) {
                folder2.mkdir();
            }
            File folder3 = new File(outputFolder + "/time_before_vm_migration");
            if (!folder3.exists()) {
                folder3.mkdir();
            }
            File folder4 = new File(outputFolder + "/metrics");
            if (!folder4.exists()) {
                folder4.mkdir();
            }

            StringBuilder data = new StringBuilder();
            String delimeter = ",";

            data.append(experimentName + delimeter);
            data.append(parseExperimentName(experimentName));
            data.append(String.format("%d", numberOfHosts) + delimeter);
            data.append(String.format("%d", numberOfVms) + delimeter);
            data.append(String.format("%.2f", totalSimulationTime) + delimeter);
            data.append(String.format("%.5f", energy) + delimeter);
            data.append(String.format("%d", numberOfMigrations) + delimeter);
            data.append(String.format("%.10f", sla) + delimeter);
            data.append(String.format("%.10f", slaTimePerActiveHost) + delimeter);
            data.append(String.format("%.10f", slaDegradationDueToMigration) + delimeter);
            data.append(String.format("%.10f", slaOverall) + delimeter);
            data.append(String.format("%.10f", slaAverage) + delimeter);
            // data.append(String.format("%.5f", slaTimePerVmWithMigration) + delimeter);
            // data.append(String.format("%.5f", slaTimePerVmWithoutMigration) + delimeter);
            // data.append(String.format("%.5f", slaTimePerHost) + delimeter);
            data.append(String.format("%d", numberOfHostShutdowns) + delimeter);
            data.append(String.format("%.2f", meanTimeBeforeHostShutdown) + delimeter);
            data.append(String.format("%.2f", stDevTimeBeforeHostShutdown) + delimeter);
            data.append(String.format("%.2f", meanTimeBeforeVmMigration) + delimeter);
            data.append(String.format("%.2f", stDevTimeBeforeVmMigration) + delimeter);

            if (datacenter.getVmAllocationPolicy() instanceof PowerVmAllocationPolicyMigrationAbstract) {
                PowerVmAllocationPolicyMigrationAbstract vmAllocationPolicy = (PowerVmAllocationPolicyMigrationAbstract) datacenter
                        .getVmAllocationPolicy();

                double executionTimeVmSelectionMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryVmSelection());
                double executionTimeVmSelectionStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryVmSelection());
                double executionTimeHostSelectionMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryHostSelection());
                double executionTimeHostSelectionStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryHostSelection());
                double executionTimeVmReallocationMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryVmReallocation());
                double executionTimeVmReallocationStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryVmReallocation());
                double executionTimeTotalMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryTotal());
                double executionTimeTotalStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryTotal());

                data.append(String.format("%.5f", executionTimeVmSelectionMean) + delimeter);
                data.append(String.format("%.5f", executionTimeVmSelectionStDev) + delimeter);
                data.append(String.format("%.5f", executionTimeHostSelectionMean) + delimeter);
                data.append(String.format("%.5f", executionTimeHostSelectionStDev) + delimeter);
                data.append(String.format("%.5f", executionTimeVmReallocationMean) + delimeter);
                data.append(String.format("%.5f", executionTimeVmReallocationStDev) + delimeter);
                data.append(String.format("%.5f", executionTimeTotalMean) + delimeter);
                data.append(String.format("%.5f", executionTimeTotalStDev) + delimeter);

                writeMetricHistory(hosts, vmAllocationPolicy, outputFolder + "/metrics/" + experimentName
                        + "_metric");
            }

            data.append("\n");

            writeDataRow(data.toString(), outputFolder + "/stats/" + experimentName + "_stats.csv");
            writeDataColumn(timeBeforeHostShutdown, outputFolder + "/time_before_host_shutdown/"
                    + experimentName + "_time_before_host_shutdown.csv");
            writeDataColumn(timeBeforeVmMigration, outputFolder + "/time_before_vm_migration/"
                    + experimentName + "_time_before_vm_migration.csv");

        } else {
            Log.setDisabled(false);                                                                        //设置禁用输出标志位false，即可以输出
            Log.printLine();                                                                               //打印消息和换行
            Log.printLine(String.format("Experiment name: " + experimentName));                            //打印：实验名称
            Log.printLine(String.format("Number of hosts: " + numberOfHosts));                             //打印：主机数量
            Log.printLine(String.format("Number of VMs: " + numberOfVms));                                 //打印：虚拟机数量
            Log.printLine(String.format("Total simulation time: %.2f sec", totalSimulationTime));          //打印：总共仿真时间
            Log.printLine(String.format("Energy consumption: %.2f kWh", energy));                          //打印：功率消耗
            Log.printLine(String.format("Number of VM migrations: %d", numberOfMigrations));               //打印：虚拟机迁移数量
            Log.printLine(String.format("SLA: %.5f%%", sla * 100));                                        //打印：SLA指标
            Log.printLine(String.format(
                    "SLA perf degradation due to migration: %.2f%%",
                    slaDegradationDueToMigration * 100));                                                   //打印：由于虚拟机迁移导致的SLA性能指标下降
            Log.printLine(String.format("SLA time per active host: %.2f%%", slaTimePerActiveHost * 100));   //打印：每个活跃主机的SLA指标
            Log.printLine(String.format("Overall SLA violation: %.2f%%", slaOverall * 100));                //打印：总体违反SLA指标
            Log.printLine(String.format("Average SLA violation: %.2f%%", slaAverage * 100));                //打印：平均违反SLA指标
            // Log.printLine(String.format("SLA time per VM with migration: %.2f%%",
            // slaTimePerVmWithMigration * 100));
            // Log.printLine(String.format("SLA time per VM without migration: %.2f%%",
            // slaTimePerVmWithoutMigration * 100));
            // Log.printLine(String.format("SLA time per host: %.2f%%", slaTimePerHost * 100));
            Log.printLine(String.format("Number of host shutdowns: %d", numberOfHostShutdowns));             //打印：主机关机的数量
            Log.printLine(String.format(
                    "Mean time before a host shutdown: %.2f sec",
                    meanTimeBeforeHostShutdown));                                                            //打印：主机开机的时间的平均值
            Log.printLine(String.format(
                    "StDev time before a host shutdown: %.2f sec",
                    stDevTimeBeforeHostShutdown));                                                           //打印：主机开机的时间的标准偏差
            Log.printLine(String.format(
                    "Mean time before a VM migration: %.2f sec",
                    meanTimeBeforeVmMigration));                                                              //打印：虚拟机未迁移时间的平均值
            Log.printLine(String.format(
                    "StDev time before a VM migration: %.2f sec",
                    stDevTimeBeforeVmMigration));                                                             //打印：虚拟机未迁移时间的标准偏差

            if (datacenter.getVmAllocationPolicy() instanceof PowerVmAllocationPolicyMigrationAbstract) { //判断数据中心的虚拟机分配策略对象是否为PowerVmAllocationPolicyMigrationAbstract的实例
                //instanceof作用：判断左边对象是否为右边类的实例，返回一个boolean类型值。

                PowerVmAllocationPolicyMigrationAbstract vmAllocationPolicy = (PowerVmAllocationPolicyMigrationAbstract) datacenter
                        .getVmAllocationPolicy();                                                         //PowerVmAllocationPolicyMigrationAbstract是一个抽象类，是一个可以使用迁移--动态优化虚拟机分配的虚拟机分配策略的抽象类
                //获取数据中心的虚拟机分配策略，并赋值给vmAllocationPolicy

                double executionTimeVmSelectionMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryVmSelection());                                           //得到虚拟机选择的执行时间历史记录，并求平均值
                double executionTimeVmSelectionStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryVmSelection());                                           //得到虚拟机选择的执行时间历史记录，并求标准偏差
                double executionTimeHostSelectionMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryHostSelection());                                          //得到主机选择的执行时间历史记录，并求平均值
                double executionTimeHostSelectionStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryHostSelection());                                          //得到主机选择的执行时间历史记录，并求标准偏差
                double executionTimeVmReallocationMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryVmReallocation());                                         //得到虚拟机重新分配的执行时间历史记录，并求平均值
                double executionTimeVmReallocationStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryVmReallocation());                                         //得到虚拟机重新分配的执行时间历史记录，并求标准偏差
                double executionTimeTotalMean = MathUtil.mean(vmAllocationPolicy
                        .getExecutionTimeHistoryTotal());                                                  //得到总执行时间历史记录，并求平均值
                double executionTimeTotalStDev = MathUtil.stDev(vmAllocationPolicy
                        .getExecutionTimeHistoryTotal());                                                  //得到总执行时间历史记录，并求标准偏差

                Log.printLine(String.format(
                        "Execution time - VM selection mean: %.5f sec",
                        executionTimeVmSelectionMean));                                         //打印：虚拟机选择的执行时间平均值
                Log.printLine(String.format(
                        "Execution time - VM selection stDev: %.5f sec",
                        executionTimeVmSelectionStDev));                                        //打印：虚拟机选择的执行时间标准偏差
                Log.printLine(String.format(
                        "Execution time - host selection mean: %.5f sec",
                        executionTimeHostSelectionMean));                                       //打印：主机选择的执行时间平均值
                Log.printLine(String.format(
                        "Execution time - host selection stDev: %.5f sec",
                        executionTimeHostSelectionStDev));                                      //打印：主机选择的执行时间标准偏差
                Log.printLine(String.format(
                        "Execution time - VM reallocation mean: %.5f sec",
                        executionTimeVmReallocationMean));                                      //打印：虚拟机重新分配的执行时间平均值
                Log.printLine(String.format(
                        "Execution time - VM reallocation stDev: %.5f sec",
                        executionTimeVmReallocationStDev));                                     //打印：虚拟机重新分配的执行时间标准偏差
                Log.printLine(String.format("Execution time - total mean: %.5f sec", executionTimeTotalMean));           //打印：总执行时间平均值
                Log.printLine(String
                        .format("Execution time - total stDev: %.5f sec", executionTimeTotalStDev));                     //打印：总执行时间标准偏差
            }
            Log.printLine();
        }

        Log.setDisabled(true);                                         //设置禁用输出标志位true，即不可以输出
    }
}