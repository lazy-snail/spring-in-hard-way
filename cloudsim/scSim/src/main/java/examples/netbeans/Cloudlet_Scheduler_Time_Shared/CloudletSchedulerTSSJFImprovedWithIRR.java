package examples.netbeans.Cloudlet_Scheduler_Time_Shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;

class ResCloudletComparator implements Comparator<ResCloudlet>{

    @Override
    public int compare(ResCloudlet t, ResCloudlet t1) {
        return t.getCloudletLength() <= t1.getCloudletLength() ? -1 : 1;
    }
    
}

public class CloudletSchedulerTSSJFImprovedWithIRR extends CloudletScheduler {

    /**
     * The cloudlet exec list.
     */
    private List<? extends ResCloudlet> cloudletExecList;

    /**
     * The cloudlet paused list.
     */
    private List<? extends ResCloudlet> cloudletPausedList;

    /**
     * The cloudlet finished list.
     */
    private List<? extends ResCloudlet> cloudletFinishedList;

    private boolean sortedExec = false;
    
    /**
     * The current cp us.
     */
    protected int currentCPUs;
    protected int TIME_QUANTA_SIZE = 5;

    public void setTIME_QUANTA_SIZE(int TIME_QUANTA_SIZE) {
        this.TIME_QUANTA_SIZE = TIME_QUANTA_SIZE;
    }

    /**
     * Creates a new CloudletSchedulerTimeShared object. This method must be
     * invoked before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerTSSJFImprovedWithIRR() {
        super();
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        currentCPUs = 0;
    }

    /**
     * Updates the processing of cloudlets running under management of this
     * scheduler.
     *
     * @param currentTime current simulation time
     * @param mipsShare array with MIPS share of each processor available to the
     * scheduler
     * @return time predicted completion time of the earliest finishing
     * cloudlet, or 0 if there is no next events
     * @pre currentTime >= 0
     * @post $none
     */
    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        setCurrentMipsShare(mipsShare);
        double timeSpan = currentTime - getPreviousTime();
        
        int no_of_quantas = (int) Math.ceil(timeSpan*1.0/TIME_QUANTA_SIZE);
        timeSpan = TIME_QUANTA_SIZE * no_of_quantas;

        for (ResCloudlet rcl : getCloudletExecList()) {
            rcl.updateCloudletFinishedSoFar((long) (getCapacity(mipsShare) * timeSpan * rcl.getNumberOfPes() * Consts.MILLION));
        }

        if (getCloudletExecList().isEmpty()) {
            setPreviousTime(currentTime);
            return 0.0;
        }

        // check finished cloudlets
        double nextEvent = Double.MAX_VALUE;
        long instructions_in_one_quanta = (long) (getCapacity(mipsShare) * TIME_QUANTA_SIZE * Consts.MILLION);
        List<ResCloudlet> toRemove = new ArrayList<>();
        if(!sortedExec) {
                    Collections.sort(getCloudletExecList(), new ResCloudletComparator());
                    sortedExec = true;
        }
        for (ResCloudlet rcl : getCloudletExecList()) {
            long remainingLength = rcl.getRemainingCloudletLength();
            if (remainingLength == 0 ||
                    remainingLength <= instructions_in_one_quanta*rcl.getNumberOfPes()) {// finished: remove from the list
                if(remainingLength > 0)
                    rcl.updateCloudletFinishedSoFar(remainingLength);
                toRemove.add(rcl);
                cloudletFinish(rcl);
            }
        }
        getCloudletExecList().removeAll(toRemove);

        // estimate finish time of cloudlets
        for (ResCloudlet rcl : getCloudletExecList()) {
            double estimatedFinishTime = currentTime
                    + (rcl.getRemainingCloudletLength() / (getCapacity(mipsShare) * rcl.getNumberOfPes()));
            if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
                estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
            }

            if (estimatedFinishTime < nextEvent) {
                nextEvent = estimatedFinishTime;
            }
        }

        setPreviousTime(currentTime);
        return nextEvent;
    }

    /**
     * Gets the capacity.
     *
     * @param mipsShare the mips share
     * @return the capacity
     */
    protected double getCapacity(List<Double> mipsShare) {
        double capacity = 0.0;
        int cpus = 0;
        for (Double mips : mipsShare) {
            capacity += mips;
            if (mips > 0.0) {
                cpus++;
            }
        }
        currentCPUs = cpus;

        int pesInUse = 0;
        for (ResCloudlet rcl : getCloudletExecList()) {
            pesInUse += rcl.getNumberOfPes();
        }

        if (pesInUse > currentCPUs) {
            capacity /= pesInUse;
        } else {
            capacity /= currentCPUs;
        }
        return capacity;
    }

    /**
     * Cancels execution of a cloudlet.
     *
     * @param cloudletId ID of the cloudlet being cancealed
     * @return the canceled cloudlet, $null if not found
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet cloudletCancel(int cloudletId) {
        boolean found = false;
        int position = 0;

        // First, looks in the finished queue
        found = false;
        for (ResCloudlet rcl : getCloudletFinishedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            return getCloudletFinishedList().remove(position).getCloudlet();
        }

        // Then searches in the exec list
        position = 0;
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            ResCloudlet rcl = getCloudletExecList().remove(position);
            if (rcl.getRemainingCloudletLength() == 0) {
                cloudletFinish(rcl);
            } else {
                rcl.setCloudletStatus(Cloudlet.CANCELED);
            }
            return rcl.getCloudlet();
        }

        // Now, looks in the paused queue
        found = false;
        position = 0;
        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                rcl.setCloudletStatus(Cloudlet.CANCELED);
                break;
            }
            position++;
        }

        if (found) {
            return getCloudletPausedList().remove(position).getCloudlet();
        }

        return null;
    }

    /**
     * Pauses execution of a cloudlet.
     *
     * @param cloudletId ID of the cloudlet being paused
     * @return $true if cloudlet paused, $false otherwise
     * @pre $none
     * @post $none
     */
    @Override
    public boolean cloudletPause(int cloudletId) {
        boolean found = false;
        int position = 0;

        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            // remove cloudlet from the exec list and put it in the paused list
            ResCloudlet rcl = getCloudletExecList().remove(position);
            if (rcl.getRemainingCloudletLength() == 0) {
                cloudletFinish(rcl);
            } else {
                rcl.setCloudletStatus(Cloudlet.PAUSED);
                getCloudletPausedList().add(rcl);
            }
            return true;
        }
        return false;
    }

    /**
     * Processes a finished cloudlet.
     *
     * @param rcl finished cloudlet
     * @pre rgl != $null
     * @post $none
     */
    @Override
    public void cloudletFinish(ResCloudlet rcl) {
        rcl.setCloudletStatus(Cloudlet.SUCCESS);
        rcl.finalizeCloudlet();
        getCloudletFinishedList().add(rcl);
    }

    /**
     * Resumes execution of a paused cloudlet.
     *
     * @param cloudletId ID of the cloudlet being resumed
     * @return expected finish time of the cloudlet, 0.0 if queued
     * @pre $none
     * @post $none
     */
    @Override
    public double cloudletResume(int cloudletId) {
        boolean found = false;
        int position = 0;

        // look for the cloudlet in the paused list
        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            ResCloudlet rgl = getCloudletPausedList().remove(position);
            rgl.setCloudletStatus(Cloudlet.INEXEC);
            getCloudletExecList().add(rgl);

			// calculate the expected time for cloudlet completion
            // first: how many PEs do we have?
            double remainingLength = rgl.getRemainingCloudletLength();
            double estimatedFinishTime = CloudSim.clock()
                    + (remainingLength / (getCapacity(getCurrentMipsShare()) * rgl.getNumberOfPes()));

            return estimatedFinishTime;
        }

        return 0.0;
    }

    /**
     * Receives an cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cloudlet the submited cloudlet
     * @param fileTransferTime time required to move the required files from the
     * SAN to the VM
     * @return expected finish time of this cloudlet
     * @pre gl != null
     * @post $none
     */
    @Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        ResCloudlet rcl = new ResCloudlet(cloudlet);
        rcl.setCloudletStatus(Cloudlet.INEXEC);
        for (int i = 0; i < cloudlet.getNumberOfPes(); i++) {
            rcl.setMachineAndPeId(0, i);
        }

        getCloudletExecList().add(rcl);

        // use the current capacity to estimate the extra amount of
        // time to file transferring. It must be added to the cloudlet length
        double extraSize = getCapacity(getCurrentMipsShare()) * fileTransferTime;
        long length = (long) (cloudlet.getCloudletLength() + extraSize);
        cloudlet.setCloudletLength(length);
        sortedExec = false;
        return cloudlet.getCloudletLength() / getCapacity(getCurrentMipsShare());
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.CloudletScheduler#cloudletSubmit(cloudsim.Cloudlet)
     */
    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }

    /**
     * Gets the status of a cloudlet.
     *
     * @param cloudletId ID of the cloudlet
     * @return status of the cloudlet, -1 if cloudlet not found
     * @pre $none
     * @post $none
     */
    @Override
    public int getCloudletStatus(int cloudletId) {
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus();
            }
        }
        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus();
            }
        }
        return -1;
    }

    /**
     * Get utilization created by all cloudlets.
     *
     * @param time the time
     * @return total utilization
     */
    @Override
    public double getTotalUtilizationOfCpu(double time) {
        double totalUtilization = 0;
        for (ResCloudlet gl : getCloudletExecList()) {
            totalUtilization += gl.getCloudlet().getUtilizationOfCpu(time);
        }
        return totalUtilization;
    }

    /**
     * Informs about completion of some cloudlet in the VM managed by this
     * scheduler.
     *
     * @return $true if there is at least one finished cloudlet; $false
     * otherwise
     * @pre $none
     * @post $none
     */
    @Override
    public boolean isFinishedCloudlets() {
        return getCloudletFinishedList().size() > 0;
    }

    /**
     * Returns the next cloudlet in the finished list, $null if this list is
     * empty.
     *
     * @return a finished cloudlet
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet getNextFinishedCloudlet() {
        if (getCloudletFinishedList().size() > 0) {
            return getCloudletFinishedList().remove(0).getCloudlet();
        }
        return null;
    }

    /**
     * Returns the number of cloudlets runnning in the virtual machine.
     *
     * @return number of cloudlets runnning
     * @pre $none
     * @post $none
     */
    @Override
    public int runningCloudlets() {
        return getCloudletExecList().size();
    }

    /**
     * Returns one cloudlet to migrate to another vm.
     *
     * @return one running cloudlet
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet migrateCloudlet() {
        ResCloudlet rgl = getCloudletExecList().remove(0);
        rgl.finalizeCloudlet();
        return rgl.getCloudlet();
    }

    /**
     * Gets the cloudlet exec list.
     *
     * @param <T> the generic type
     * @return the cloudlet exec list
     */
    @SuppressWarnings("unchecked")
    public <T extends ResCloudlet> List<T> getCloudletExecList() {
        return (List<T>) cloudletExecList;
    }

    /**
     * Sets the cloudlet exec list.
     *
     * @param <T> the generic type
     * @param cloudletExecList the new cloudlet exec list
     */
    protected <T extends ResCloudlet> void setCloudletExecList(List<T> cloudletExecList) {
        this.cloudletExecList = cloudletExecList;
    }

    /**
     * Gets the cloudlet paused list.
     *
     * @param <T> the generic type
     * @return the cloudlet paused list
     */
    @SuppressWarnings("unchecked")
    public <T extends ResCloudlet> List<T> getCloudletPausedList() {
        return (List<T>) cloudletPausedList;
    }

    /**
     * Sets the cloudlet paused list.
     *
     * @param <T> the generic type
     * @param cloudletPausedList the new cloudlet paused list
     */
    protected <T extends ResCloudlet> void setCloudletPausedList(List<T> cloudletPausedList) {
        this.cloudletPausedList = cloudletPausedList;
    }

    /**
     * Gets the cloudlet finished list.
     *
     * @param <T> the generic type
     * @return the cloudlet finished list
     */
    @SuppressWarnings("unchecked")
    public <T extends ResCloudlet> List<T> getCloudletFinishedList() {
        return (List<T>) cloudletFinishedList;
    }

    /**
     * Sets the cloudlet finished list.
     *
     * @param <T> the generic type
     * @param cloudletFinishedList the new cloudlet finished list
     */
    protected <T extends ResCloudlet> void setCloudletFinishedList(List<T> cloudletFinishedList) {
        this.cloudletFinishedList = cloudletFinishedList;
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.CloudletScheduler#getCurrentRequestedMips()
     */
    @Override
    public List<Double> getCurrentRequestedMips() {
        List<Double> mipsShare = new ArrayList<Double>();
        return mipsShare;
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.CloudletScheduler#getTotalCurrentAvailableMipsForCloudlet(cloudsim.ResCloudlet,
     * java.util.List)
     */
    @Override
    public double getTotalCurrentAvailableMipsForCloudlet(ResCloudlet rcl, List<Double> mipsShare) {
        return getCapacity(getCurrentMipsShare());
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.CloudletScheduler#getTotalCurrentAllocatedMipsForCloudlet(cloudsim.ResCloudlet,
     * double)
     */
    @Override
    public double getTotalCurrentAllocatedMipsForCloudlet(ResCloudlet rcl, double time) {
        return 0.0;
    }

    /*
     * (non-Javadoc)
     * @see cloudsim.CloudletScheduler#getTotalCurrentRequestedMipsForCloudlet(cloudsim.ResCloudlet,
     * double)
     */
    @Override
    public double getTotalCurrentRequestedMipsForCloudlet(ResCloudlet rcl, double time) {
        // TODO Auto-generated method stub
        return 0.0;
    }

    @Override
    public double getCurrentRequestedUtilizationOfRam() {
        double ram = 0;
        for (ResCloudlet cloudlet : cloudletExecList) {
            ram += cloudlet.getCloudlet().getUtilizationOfRam(CloudSim.clock());
        }
        return ram;
    }

    @Override
    public double getCurrentRequestedUtilizationOfBw() {
        double bw = 0;
        for (ResCloudlet cloudlet : cloudletExecList) {
            bw += cloudlet.getCloudlet().getUtilizationOfBw(CloudSim.clock());
        }
        return bw;
    }

}