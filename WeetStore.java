/**
 * This WeetStore consists of a Hashmap implementation, adapted from Lab4, which has a Key-Value pair.
 * The key is the user Id and the corresponding value is the given Weet Object.
 * The hasmap consists of 3 main methods: put, get and hash. The put and get are self explanatory whilst the hash is used to reduced the
   probability of collisions.
* A initial size of 250, I believed, to be a suitable size as it doesn't use unnecessary memory whilst being significantly large to begin with.
* I chose a hashmap for this implementation due to it being faster for e.g. searching for a Id than e.g. a self-balancing binary tree. This is due
  to hashmaps having a best case complexity of O(1) whereas the average, binary tree would have O(log(n)). The biggest disadvantage is that unlike a binary tree, hashmaps need to be resized but I believe, I overcame this problem to some extent with the intial size being 250.
 * @author: u1813057
 */

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;
import uk.ac.warwick.java.cs126.models.Weet;

import java.io.BufferedReader;
import java.util.Date;
import java.io.FileReader;
import java.text.ParseException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class WeetStore implements IWeetStore {
    /*
    *This assigns the variable name weetInfo to the Hashmap type where the key is of type Integer and the value of Type Weet.
    */
    private HashMap<Integer, Weet> weetInfo;

    /*
    * The hashmap class adapted from Lab4 where a private array is initialised that will contain the Key-Value pair.
    * A initial capacity of 250 is chosen for the hasmap.
    * No_users is initialised which will store the number of weets that are currently stored.
    */
    class HashMap<K, V> {
        private dictionary<K, V>[] list;
        private int size = 250;
        private int no_weets;

        /*
        * The inner class for the Key-Value pair which consists of the key(user Id) and value (Weet object) as well as the next key-value
        * pair in the list.
        */
        class dictionary<K, V> {
            V value;;
            final K key;
            dictionary<K, V> next;

            /*
            * The first constructor is for when a new Key-Value pair is initialised.
            */
            public dictionary(K key, V value, dictionary<K, V> next) {
                this.next = next;
                this.key = key;
                this.value = value;
            }
        }

        /*
        * The second constructor is for the Hashmap and it sets the number of users to 0 to start with.
        * It also creates the intial array of Key-Value pairs with a size of 250 given above.
        */
        @SuppressWarnings("unchecked")
        public HashMap() {
            this.no_weets = 0;
            this.list = new dictionary[size];
        }

        /*
        * This method is used to put a Weet Object into the Hashmap.
        * First, a new Key-Value pair Object is constructed of a key, value and next which in this case points to null.
        * The key is hashed and the value is stored in the variable bucket.
        * If there is already a weet located in the hashed index, until there is a null element in the array, it will go through all the elements
          to see if there is a match with the given key. If there is, return false as the weet Id is not unique.
        * If the weet ID is unique, the end of the array will be reached and the next key-value pair will be assigned after this.
        * Once the User has been assigned that space, the counter will increase to reflect the increment by 1 in no_weets.
        * if the hashed value index contains no weets then the Key-Value pair is assigned to the first space and the count is incremented
          by 1 to reflect this and true is returned.
        */
        public boolean put(K key, V value) {
            dictionary<K, V> entry = new dictionary<>(key, value, null);
            int bucket = hash(key);
            dictionary<K, V> existing = list[bucket];

            if (existing != null) {
                while (existing.next != null) {
                    if (existing.key.equals(key)) {
                        existing.value = value;
                        return false;
                    }
                }
                existing = existing.next;
                existing.next = entry;
                no_weets++;
                return true;
            }
            else {
                list[bucket] = entry;
                no_weets++;
                return true;
            }
        }

        /*
        * This method is used to get A Weet Object from the hashmap when given the Key(Weet Id) as the parameter.
        * The key is hashed and the value is stored in the varaible bucket.
        * The first Key-Value pair is assigned to the temp Object.
        * The array of Key-Value pairs is searched until a null value is reached implying the end of the array, once the hashed key matcheds
          the key entered as a parameter, the value associated with that key is returned.
          * In the case, the key is not found, null is returned.
        */
        public V get(K key) {
            int bucket = hash(key);
            dictionary<K,V> temp = list[bucket];
            while (temp != null) {
                if (temp.key.equals(key)) {
                    return temp.value;
                }
                temp = temp.next;
                return null;
            }
            return null;
        }

        /*
        * This methods gets the top 10 trending weets.
        * An array is made that contains all the weets with a hashtag and another that is of size 10 to store the trending weets.
        * A 2d array is created to store the weet in the first column and the number of times it appears in the second column.
        * All the values in the 2d array are initialised to 0 or null so if there aren't exactly 10 trending weets, then an error wont occur and null will be displayed..
        * All the messages are split up according to the spaces and for every split up message, if it cointains a hashtag
          then it is added to the 2d array and counted to see if its trending.
        * The next time a weet is found, the 2d array is scanned to see if it is already stored and if it is, then it adds to the counter
          in the 1st column corresponding to the weet's row.
        * If the word is not found, it is added to the 1st column in the 1d array and initialised to 1.
        */
        public String[] trendingWeets() {

            int b = 0;
            boolean found = false;

            Weet[] tagWeets = weetSearch("#");
            String[] trendingWeets = new String[10];
            Weet[] allWeets = new Weet[getLength(tagWeets)];
            Object trendingCount[][] = new Object[allWeets.length][2];
            for (int i = 0; i < getLength(tagWeets); i++) {
                allWeets[i] = tagWeets[i];
            }

            for(int i = 0; i < getLength(trendingCount); i++) {
                trendingCount[i][0] = null;
                trendingCount[i][1] = 0;
            }

            for (int i = 0; i < getLength(allWeets); i++) {
                String[] temp = allWeets[i].getMessage().split("\\s+");
                for (int k = 0; k < getLength(temp); k++) {
                    found = false;
                    b = 0;
                    if (temp[k].startsWith("#")) {
                        while (trendingCount[b][0]!=null) {
                            if (trendingCount[b][0].equals(temp[k])) {
                                trendingCount[b][1] = (int)trendingCount[b][1] + 1;
                                found = true;
                                break;
                            }
                            b++;
                        }
                        if (found == false) {
                            trendingCount[b][1] = (int) trendingCount[b][1] + 1;
                            trendingCount[b][0] = (String) temp[k];
                        }
                    }
                }
            }

            sort(trendingCount, 0, trendingCount.length - 1);

            for (int i = 0; i < trendingWeets.length; i++) {
                trendingWeets[i] = (String) trendingCount[i][0];
            }

            String[] updatedTrending = new String[getLength(trendingWeets)];

            for (int i = 0; i < getLength(trendingWeets); i++){
                updatedTrending[i] = (String) trendingWeets[i];
            }
            return updatedTrending;
        }

        /*
        * This methods gets all the Weets.
        * A new array is created of size no_weets which is the number of weets that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, all non null elements from the Hashmap are added to the array.
        * The array with all the weets added is then returned.
        */
        public Weet[] getallWeets() {
            Weet[] fixed_array = new Weet[no_weets];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    fixed_array[j++] = (Weet) head.value;
                    head = head.next;
                }
                i++;
            }
            return fixed_array;
        }

        /*
        * This methods gets all the Weets from a the user which is entered as a parameter.
        * A new array is created of size no_weets which is the number of weets that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, all non null elements from the Hashmap that have the same id as that of the
          parameter added to the array.
        * The array with all the weets added is then returned.
        */
        public Weet[] userWeets(int uid) {
            Weet[] fixed_array = new Weet[no_weets];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    Weet weet = (Weet) head.value;
                    if (weet.getUserId() == uid){
                        fixed_array[j++] = (Weet) head.value;
                    }
                    head = head.next;
                }
                i++;
            }
            return fixed_array;
        }

        /*
        * This method gets all the Weets which fulfill the search criteria.
        * A new array is created of size no_weets which is the number of weets that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, the name of the temp variable is compared to the search query.
        * If this search query is met, it is added to the array and the next value from the list of weets is compared.
        * The array with all the weets that meet the search query are added and is then returned.
        */
        public Weet[] weetSearch(String query) {
            Weet[] fixed_array = new Weet[no_weets];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    Weet weet = (Weet) head.value;
                    if (weet.getMessage().contains(query)){
                        fixed_array[j++] = (Weet) head.value;
                    }
                    head = head.next;
                }
                i++;
            }
            return fixed_array;
        }

        /*
        * This method is used to hash the Key.
        * The hash is modded by the size as this prevents Array out of bounds errors.
        */
        private int hash(K key){
            return Math.abs(key.hashCode()) % size;
        }

        /*
        * This method gets all the weets which fulfill the date criteria.
        * A new array is created of size no_weets which is the number of weets that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, the name of the temp variable is compared to the date given as a paramter.
        * If this Date joined of the weet assigned to the temp weet is equal to the date of the date given as a parameter, then this temp weet is
          added to the array.
        * The array with all the weets that match the date parameter are added and is then returned.
        */
    public Weet[] weetOn(Date dateOn){
            Weet[] fixed_array = new Weet[no_weets];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    Weet weet = (Weet) head.value;
                    if (weet.getDateWeeted() == dateOn){
                        fixed_array[j++] = (Weet) head.value;
                    }
                    head = head.next;
                }
                i++;
            }
            return fixed_array;
    }

        /*
        * This method gets all the weets which fulfill the date criteria.
        * A new array is created of size no_weets which is the number of weets that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, the name of the temp variable is compared to the date given as a paramter.
        * If this Date joined of the weet assigned to the temp weet is before the date of the date given as a parameter, then this temp weet is
          added to the array.
        * The array with all the weets that match the date parameter are added and is then returned.
        */
    public Weet[] weetBefore(Date dateBefore){
            Weet[] fixed_array = new Weet[no_weets];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    Weet weet = (Weet) head.value;
                    if (weet.getDateWeeted().before(dateBefore)){
                        fixed_array[j++] = (Weet) head.value;
                    }
                    head = head.next;
                }
                i++;
            }
            return fixed_array;
    }

    }

    /*
    * This method is used to sort the array in a given order.
    * This was chosen as it the fastest sort compared to merge, insertion or bubble sort.
    * The quicksort method is called twice to sort in descending order so it is dated from latest to earliest.
    */
    public void quickSort(Weet[] arr, int begin, int end) {
    if (begin < end) {
        int partitionIndex = partition(arr, begin, end);

        quickSort(arr, begin, partitionIndex-1);
        quickSort(arr, partitionIndex+1, end);
    }
    }

    /*
    * This method is used with the quicksort method.
    * The pivot of type Weet is assigned to the last element of the array given as a parameter,
    * Whilst the integer paramater begin is less than the end, if the element with a lower index has a date joined of after that than
      the date joined of the elemnt assigned to the pivot then the lower index element is assigned to a temp variable. Since the lower index element has a date joined of after the higher index element, these two elements switch index position so that these two dates are in order. This will loop until all elements are sorted by date. Once this occurs, the element in the middle is swapped out for the element at the end so that the list is fully sorted by date.
    */
    private int partition(Weet[] arr, int begin, int end) {
    Weet pivot = arr[end];
    int i = (begin-1);

    for (int j = begin; j < end; j++) {
        if (arr[j].getDateWeeted().after(pivot.getDateWeeted())) {
            i++;

            Weet swapTemp = arr[i];
            arr[i] = arr[j];
            arr[j] = swapTemp;
        }
    }

    Weet swapTemp = arr[i+1];
    arr[i+1] = arr[end];
    arr[end] = swapTemp;

    return i+1;
    }

    /*
    * This method is used to sort the array in a given order.
    * This was chosen as it the fastest sort compared to merge, insertion or bubble sort.
    * The quicksort method is called twice to sort in descending order so it is dated from latest to earliest.
    */
    public void sort(Object[][] arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partitions(arr, begin, end);

            sort(arr, begin, partitionIndex-1);
            sort(arr, partitionIndex+1, end);
        }
        }

    /*
    * This method is used with the quicksort method.
    * The pivot of type Weet is assigned to the last element of the array given as a parameter,
    * Whilst the integer paramater begin is less than the end, if the element with a lower index has a date joined of after that than
      the date joined of the elemnt assigned to the pivot then the lower index element is assigned to a temp variable. Since the lower index element has a date joined of after the higher index element, these two elements switch index position so that these two dates are in order. This will loop for all elements until they're sorted by date. Since this is a 2d array, this is done for both columns of index 0 and 1 so that all values in a column are moving simultaneously. Once this occurs, the element in the middle is swapped out for the element at the end so that the list is fully sorted by date.
    */
        private int partitions(Object[][] arr, int begin, int end) {
        int pivot = (int) arr[end][1];
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if ((int)arr[j][1] > pivot) {
                i++;

                Object swapId = arr[i][0];
                Object swapVal = arr[i][1];
                arr[i][0] = arr[j][0];
                arr[i][1] = arr[j][1];
                arr[j][1] = swapVal;
                arr[j][0] = swapId;
            }
        }

        Object swapVal = arr[i+1][1];
        Object swapId = arr[i+1][0];
        arr[i+1][0] = arr[end][0];
        arr[i+1][1] = arr[end][1];
        arr[end][1] = swapVal;
        arr[end][0] = swapId;

        return i+1;
    }

    /*
    * This method counts the number of not null elements in an array.
    * All the elements in the given array are looped through and for every element that does not equal null a count is incremented by 1.
    * This count is returned.
    */
    public static <T> int getLength(T[] arr){
        int count = 0;
        for(T el : arr)
            if (el != null)
                ++count;
        return count;
    }

    /*
    * This is a constructor for WeetStore.
    * A new instance of a Hashmap is created which consists of a Key-Value pair and is assigned to the variable weetInfo.
    */
    public WeetStore() {
        weetInfo = new HashMap<Integer, Weet>();
    }

    /*
    * This method is for add a weet into the Hashmap.
    * The key entered into the hasmap is the Id associated with the weet and the value is the weet object given as a parameter.
    */
    public boolean addWeet(Weet weet) {
        return weetInfo.put(weet.getId(), weet);
    }

    /*
    * This method is to get the value associate with the Weet Id which is the key.
    */
    public Weet getWeet(int wid) {
        return weetInfo.get(wid);
    }

    /*
    * This method gets all the weets.
    * This calls the method which gets all the weets and clones all the users associated with the weets into a new array.
    * The array of the weets is sorted by date with the latest first then returned.
    */
    public Weet[] getWeets() {
        Weet[] allWeets = weetInfo.getallWeets().clone();
        quickSort(allWeets, 0, getLength(allWeets)-1);
        return allWeets;
    }

    /*
    * This method gets all the weets which fulfill the paramter which is the user Id..
    * This calls the method which gets all the weets and clones all the useres associated with the weets into a new array.
    * A new array is created which will store the users that have the same Id as the parameter which is of size of all the users added.
    * All the weets that meet the criteria are put into an array.
    * All the weets are sorted by date from latest to earliest and then returned.
    */
    public Weet[] getWeetsByUser(User usr) {
        Weet[] allWeets = weetInfo.userWeets(usr.getId());
        Weet[] meetsQuery = new Weet[getLength(allWeets)];
        for (int i = 0; i < getLength(allWeets); i++){
            meetsQuery[i] = allWeets[i];
        }
        quickSort(meetsQuery, 0, getLength(allWeets)-1);
        return meetsQuery;
    }

    /*
    * This method gets all the weets which fulfill the search criteria.
    * This calls the method which gets all the weets and clones all the weets into a new array.
    * A new array is created which will store the weets that meet the search criteria of size of all the weets.
    * All the weets that meet the criteria are put into an array.
    * All the weets are sorted by date from latest to earliest and then returned.
    */
    public Weet[] getWeetsContaining(String query) {
        Weet[] allWeets = weetInfo.weetSearch(query);
        Weet[] meetsQuery = new Weet[getLength(allWeets)];
        for (int i = 0; i < getLength(allWeets); i++){
            meetsQuery[i] = allWeets[i];
        }
        quickSort(meetsQuery, 0, getLength(allWeets)-1);
        return meetsQuery;
    }

    /*
    * This method gets all the weets that joined on the date given as a parameter.
    * This calls the method which gets all the weets and clones all the weets into a new array.
    * A new array is created which will store the weets that have the date given as a parameter.
    * All the weets that meet the criteria are put into an array.
    * All the weets are sorted by date from latest to earliest and then returned.
    */
    public Weet[] getWeetsOn(Date dateOn) {
        Weet[] allWeets = weetInfo.weetOn(dateOn);
        Weet[] meetsQuery = new Weet[getLength(allWeets)];
        for (int i = 0; i < getLength(allWeets); i++){
            meetsQuery[i] = allWeets[i];
        }
        quickSort(meetsQuery, 0, getLength(allWeets)-1);
        return meetsQuery;
    }

    /*
    * This method gets all the weets that joined before a given date.
    * This calls the method which gets all the weets and clones all the weets into a new array.
    * A new array is created which will store the weets that have the date given as a parameter.
    * All the weets that meet the criteria are put into an array.
    * All the weets are sorted by date from latest to earliest and then returned.
    */
    public Weet[] getWeetsBefore(Date dateBefore) {
        Weet[] allWeets = weetInfo.weetBefore(dateBefore);
        Weet[] meetsQuery = new Weet[getLength(allWeets)];
        for (int i = 0; i < getLength(allWeets); i++){
            meetsQuery[i] = allWeets[i];
        }
        quickSort(meetsQuery, 0, getLength(allWeets)-1);
        return meetsQuery;
    }

    /*
    * This method gets all the top 10 trending weets which is done by calling the getTrending method.
    */
    public String[] getTrending() {
        return weetInfo.trendingWeets();
    }

}
