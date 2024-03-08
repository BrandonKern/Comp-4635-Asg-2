//pair class gotten from: https://iqcode.com/code/java/java-arraylist-of-pairs

public class Pair<Key,Value> {
    private Key key;
    private Value value;

    public Pair(Key key, Value value){
        this.key = key;
        this.value = value;
    }

    public Key getKey(){ return this.key; }
    public Value getValue(){ return this.value; }

    public void setKey(Key key){ this.key = key; }
    public void setValue(Value value){ this.value = value; }
}
