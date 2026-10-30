public class Item {

    protected String name;
    protected String category;
    protected String location;
    protected String description;

    public Item(String name, String category,
                String location, String description) {

        this.name = name;
        this.category = category;
        this.location = location;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }
}