public class Item{
	String name;
	int price;
	String description;
	int ad;
	int ap;
	void show_detail(){
		System.out.println(name + " " + price);
	}
	public static void main(String[] args) {
		Item Blood_battle = new Item();
		Blood_battle.name = "血瓶";
		Blood_battle.price = 50;
		Blood_battle.show_detail();

		Item Straw_shoe = new Item();
		Straw_shoe.name = "草鞋";
		Straw_shoe.price = 300;
		Straw_shoe.show_detail();

		Item Sword = new Item();
		Sword.name = "长剑";
		Sword.price	= 350;
		Sword.show_detail();
	}
}

