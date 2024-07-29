/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package power;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModelCubic;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumMigrationTime;
import  org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.PowerVm;
/**
 *
 * @author admin
 */
public class VMAllocation 
{
    Details dt=new Details();
    Datacenter dc1;
    DatacenterCharacteristics characteristics;
    
    public void readVM()
    {
        try
        {
            File fe=new File("vm1.txt");
            FileInputStream fis=new FileInputStream(fe);
            byte bt[]=new byte[fis.available()];
            fis.read(bt);
            fis.close();
            
            String g1=new String(bt);
            System.out.println("VM List");
            System.out.println("=========================");
            System.out.println(g1);
            String g2[]=g1.split("\n");
            for(int i=1;i<g2.length;i++)
            {
                dt.Vt.add(g2[i].trim());
            }
            
            dt.vms=new String[dt.Vt.size()][4];
             for(int i=0;i<dt.Vt.size();i++)
            {
                String a1[]=dt.Vt.get(i).toString().trim().split("\t");
                dt.vms[i][0]=a1[0];    // VM Id                    
                dt.vms[i][1]=a1[1];    // VM cpu
                dt.vms[i][2]=a1[2];    // VM ram
                dt.vms[i][3]=a1[3];    // VM bw
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
     public void readHost()
    {
        try
        {
            File fe=new File("host2.txt");
            FileInputStream fis=new FileInputStream(fe);
            byte bt[]=new byte[fis.available()];
            fis.read(bt);
            fis.close();
            
            String g1=new String(bt);
            System.out.println("Host List");
            System.out.println("=========================");
            System.out.println(g1);
            String g2[]=g1.split("\n"); 
                 
            for(int i=1;i<g2.length;i++)
            {
                dt.Ht.add(g2[i].trim());                
            }
            
            dt.host=new String[dt.Ht.size()][4];
               
            for(int i=0;i<dt.Ht.size();i++)
            {
                String a1[]=dt.Ht.get(i).toString().trim().split("\t");
                dt.host[i][0]=a1[0];    // Host Id
                dt.host[i][1]=a1[1];    // Host cpu
                dt.host[i][2]=a1[2];    // Host ram
                dt.host[i][3]=a1[3];    // Host bw                
            }  
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createHost()
    {
        try
        {
            Log.printLine("Starting CloudSim");
            CloudSim cs=new CloudSim();
            Calendar calendar = Calendar.getInstance();
            cs.init(1, calendar,false);
                
            String name="DC1";
            
            for(int i=0;i<dt.Ht.size();i++)
            {
                String a1[]=dt.Ht.get(i).toString().split("\t");
                
                int id=Integer.parseInt(a1[0]);
                int cpu=Integer.parseInt(a1[1]);
                int ram1=Integer.parseInt(a1[2]);
                int bw2=Integer.parseInt(a1[3]);
                int storage=100000;
                List<Pe> peList1 = new ArrayList<Pe>();
                int mips1 = cpu;//1000000;
                
                for(int k=0;k<cpu;k++)
                    peList1.add(new Pe(0, new PeProvisionerSimple(mips1))); 
                //peList1.add(new Pe(0, new PeProvisionerSimple(mips1))); 
                
                //dt.hostList.add(new Host(id, new RamProvisionerSimple(ram1),new BwProvisionerSimple(bw2), storage, peList1,new VmSchedulerTimeShared(peList1))); 
                dt.hostList.add(new PowerHost(id, new RamProvisionerSimple(ram1),new BwProvisionerSimple(bw2), storage, peList1,new VmSchedulerTimeShared(peList1),new PowerModelCubic(1000,500))); 
            }
            
            String arch = "x86"; 
            String os = "Linux"; 
            String vmm1 = "Xen";
            double time_zone = 10.0; 
            double cost = 3.0; 
            double costPerMem = 0.05; 
            double costPerStorage = 0.2;
									
            double costPerBw = 0.1;
            LinkedList<Storage> storageList = new LinkedList<Storage>();
                    
            characteristics = new DatacenterCharacteristics(arch, os, vmm1, dt.hostList, time_zone, cost, costPerMem,costPerStorage, costPerBw);
                   
            dc1 = new Datacenter(name, characteristics, new VmAllocationPolicySimple(dt.hostList), storageList, 0);
            System.out.println("Data Center Created with "+dt.Ht.size()+" Host");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
   
    public void createVM()
    {
        try
        {
            for(int i=0;i<dt.vms.length;i++)
            {
                int vmid = Integer.parseInt(dt.vms[i][0]);
                int cid=Integer.parseInt(dt.vms[i][0]);
                    
                int mips = 250;
                long size = 10000; //image size (MB)
                int ram = Integer.parseInt(dt.vms[i][2]); //vm memory (MB)
                long bw = Long.parseLong(dt.vms[i][3]);
                int pesNumber = Integer.parseInt(dt.vms[i][1]); //number of cpus
                String vmm = "Xen"; //VMM name
                        
                //Vm vm1 = new Vm(vmid,cid, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                PowerVm vm1 = new PowerVm(vmid,cid, mips, pesNumber, ram, bw, size,1 ,vmm, new CloudletSchedulerTimeShared(),0.5);
                
                
                System.out.println("VM-"+vmid+" is Created...");
                dt.vmlist.add(vm1);
                        
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
     public void optimiseVmAllocation()
     {
        try
        {
            
            
            for(int j=0;j<dt.hostList.size();j++)
            {
            
                PowerHost ph=dt.hostList.get(j);
                
                
                PSO ps=new PSO();
                double uti=ps.fittnessFun(ph);
                System.out.println("Utilization for Host - "+ph.getId()+" = "+uti);
            }
            
           /* for(int i=0;i<dt.vmlist.size();i++)
            {
                PowerVm vm=dt.vmlist.get(i);
                
                long vmBW=vm.getBw();
                int vmRAM=vm.getRam();
                int vmPe=vm.getNumberOfPes();
                
                for(int j=0;j<dt.hostList.size();j++)
                {
                    PowerHost ph=dt.hostList.get(j);
                    boolean bool=ph.isSuitableForVm(vm);
                    if(bool)
                    {
                        int id=ph.getId();
                        long htBW=ph.getBw();
                        int htRAM=ph.getRam();
                        long storage=ph.getStorage();
                        List<Pe> lt=ph.getPeList();
                        int htPe=ph.getNumberOfPes();
                
                         long bw=htBW-vmBW;
                        int ram=htRAM-vmRAM;
                        int pe=htPe-vmPe;
                        
                        for(int k=0;k<pe;k++)
                            lt.add(new Pe(0, new PeProvisionerSimple(pe))); 
                        
                        PowerHost newPH=new PowerHost(id, new RamProvisionerSimple(ram),new BwProvisionerSimple(bw), storage, lt,new VmSchedulerTimeShared(lt),new PowerModelCubic(1000,500));
                        System.out.println(i+" : "+j+" ===== "+ram+" : "+bw+" = "+pe);
                        //System.out.println(i+" : "+j+" ===== "+htRAM+" : "+newPH.getRam()+" = "+htBW+" : "+newPH.getBw());
                        dt.hostList.set(j, newPH);
                        
                        break;
                    }
                }
            }
            */
            
            
           /* for(int i=0;i<dt.hostList.size();i++)
            {
                PowerHost ph=dt.hostList.get(i);
               
                int id=ph.getId();
                long htBW=ph.getBw();
                int htRAM=ph.getRam();
                long storage=ph.getStorage();
                List<Pe> lt=ph.getPeList();
                int htPe=ph.getNumberOfPes();
                
                for(int j=0;j<dt.vmlist.size();j++)
                {
                    PowerVm vm=dt.vmlist.get(j);
                    
                        
                    boolean bool=ph.isSuitableForVm(vm);
                    System.out.println(i+" : "+j+" : "+bool+" : "+htBW+" : "+htRAM);//+" : "+ph.getAvailableMips()+" = "+vm.getMips()+" : "+ph.getNumberOfPes()+" = "+vm.getNumberOfPes());
                    if(bool)
                    {
                        long vmBW=vm.getBw();
                        int vmRAM=vm.getRam();
                        int vmPe=vm.getNumberOfPes();
                        
                        
                        long bw=htBW-vmBW;
                        int ram=htRAM-vmRAM;
                        int pe=htPe-vmPe;
                        
                        for(int k=0;k<pe;k++)
                            lt.add(new Pe(0, new PeProvisionerSimple(pe))); 
                        
                        PowerHost newPH=new PowerHost(id, new RamProvisionerSimple(ram),new BwProvisionerSimple(bw), storage, lt,new VmSchedulerTimeShared(lt),new PowerModelCubic(1000,500));
                        System.out.println("===== "+newPH.getRam()+" : "+newPH.getBw());
                        dt.hostList.set(i, newPH);
                    }
                }
                
            }*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
     }
}
