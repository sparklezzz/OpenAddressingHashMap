package zxd;

public class TestEqual {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String a = "qwe";
		String b = new String("qwe");
		System.out.println("qwe".equals("qwe"));
		System.out.println(a.equals("qwe"));
		System.out.println("qwe" == "qwe");
		System.out.println(a == "qwe");
		System.out.println(b == "qwe");
	}

}
