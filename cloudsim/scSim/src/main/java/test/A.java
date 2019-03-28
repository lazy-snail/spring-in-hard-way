package test;

/**
 * Created by Neil on 2019/3/28.
 */


public class A {
    public static void main(String[] args) {
        A b = new B();
        System.out.println(b.getClass().getName());
    }
}

class B extends A {

}
