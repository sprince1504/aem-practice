package com.mysite.core.lambda;

public class Countries {
    public static void main(String[] args) {
        ListCountries getcountries = () -> System.out.print("Hello");
        Calculation doCalculation = (int a , int b) -> a+b;
        Countries countries = new Countries();
        countries.getCountries(getcountries);
        countries.addition(doCalculation);
    }
    public void getCountries(ListCountries listCountries){
        listCountries.lambda();
    }
    public void addition(Calculation calculation){
        int c=  calculation.add(1 , 1);
        System.out.print(c);
    }
}