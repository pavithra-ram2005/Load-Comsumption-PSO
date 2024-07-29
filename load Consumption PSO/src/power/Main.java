/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package power;

/**
 *
 * @author admin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        VMAllocation vm=new VMAllocation();
        vm.readVM();
        vm.readHost();
        vm.createHost();
        vm.createVM();
        vm.optimiseVmAllocation();
    }
    
}
