package com.hugmount.helloboot;

import org.junit.Test;

public class TestExexcuteOrder {

	/**
	 *  实例执行原则
	 *  1. 静态块属于该类字节码, 优先有实例
	 *  2. 静态块 > 非静态块 > 构造方法 > 普通方法
	 *  3. 静态块只执行一次
	 *  4. 父类优先子类加载, 父类实例加载完毕, 才加载子类
	 */
	@Test
	public void testHello(){
		ZiLei ziLei = new ZiLei();
		ziLei.hello();
	}

	static class FuLei {
		static {
			System.out.println("父类静态代码块");
		}
		{
			System.out.println("父类非静态代码块");
		}
		public void hello (){
			System.out.println("父类普通方法");
		}
		public FuLei () {
			System.out.println("父类构造方法");
		}
	}

	static class ZiLei extends FuLei{
		static {
			System.out.println("    子类静态代码块");
		}
		{
			System.out.println("    子类非静态代码块");
		}
		public void hello (){
			System.out.println("    子类普通方法");
		}
		public ZiLei () {
			System.out.println("    子类构造方法");
		}
	}

}
