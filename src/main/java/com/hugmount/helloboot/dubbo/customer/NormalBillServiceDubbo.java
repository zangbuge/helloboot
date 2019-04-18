package com.hugmount.helloboot.dubbo.customer;

public interface NormalBillServiceDubbo {
	
	/**财务工资驳回
	 * @param salaryId
	 * @return
	 */
	String rejectSalary(String salaryId);

}
