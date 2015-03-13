package com.mcpekorea.hangul;

import java.util.Scanner;

public class Test {

	public static void main(String[] args) {
		try(Scanner sc = new Scanner(System.in)){
			String read;
			while(!(read = sc.nextLine()).equals("STOP")){
				System.out.println(Hangul.format(read, 12, 15, 16));
			}
		}
	}

}
