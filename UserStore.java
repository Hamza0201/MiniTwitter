/**
 * This Userstore consists of a Hashmap implementation, adapted from Lab4, which has a Key-Value pair.
 * The key is the user Id and the corresponding value is the given User Object.
 * The hasmap consists of 3 main methods: put, get and hash. The put and get are self explanatory whilst the hash is used to reduced the
   probability of collisions.
* A initial size of 250, I believed, to be a suitable size as it doesn't use unnecessary memory whilst being significantly large to begin with.
* I chose a hashmap for this implementation due to it being faster for e.g. searching for a Id than e.g. a self-balancing binary tree. This is due
  to hashmaps having a best case complexity of O(1) whereas the average, binary tree would have O(log(n)). The biggest disadvantage is that unlike a binary tree, hashmaps need to be resized but I believe, I overcame this problem to some extent with the intial size being 250.
 * @author: u1813057
 */

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;

import java.util.Date;

public class UserStore implements IUserStore {
    /*
    *This assigns the variable name UserInfo to the Hashmap type where the key is of type Integer and the value of Type User*/
    private HashMap<Integer, User> userInfo;

    /*
    * The hashmap class adapted from Lab4 where a private array is initialised that will contain the Key-Value pair.
    * A initial capacity of 250 is chosen for the hasmap.
    * No_users is initialised which will store the number of users that are currently stored.
    */
    class HashMap<K, V> {
        private dictionary<K, V>[] list;
        private int size = 250;
        private int no_users;

        /*
        * The inner class for the Key-Value pair which consists of the key(user Id) and value (User object) as well as the next key-value
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
            this.no_users = 0;
            this.list = new dictionary[size];
        }

        /*
        * This method is used to put a User Object into the Hashmap.
        * First, a new Key-Value pair Object is constructed of a key, value and next which in this case points to null.
        * The key is hashed and the value is stored in the variable bucket.
        * If there is already a user located in the hashed index, until there is a null element in the array, it will go through all the elements
          to see if there is a match with the given key. If there is, return false as the user Id is not unique.
        * If the user ID is unique, the end of the array will be reached and the next key-value pair will be assigned after this.
        * Once the User has been assigned that space, the counter will increase to reflect the increment by 1 in no_users.
        * if the hashed value index contains no users then the Key-Value pair is assigned to the first space and the count is incremented
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
                no_users++;
                return true;
            }
            else {
                list[bucket] = entry;
                no_users++;
                return true;
            }
        }

        /*
        * This method is used to get A User Object from the hashmap when given the Key(User Id) as the parameter.
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
        * This method is used to hash the Key.
        * The hash is modded by the size as this prevents Array out of bounds errors.
        */
        private int hash(K key){
            return Math.abs(key.hashCode()) % size;
        }

        /*
        * This methods gets all the users.
        * A new array is created of size no_useres which is the number of users that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, all non null elements from the Hashmap are added to the array.
        * The array with all the users added is then returned.
        */
        public User[] getallUsers() {
            User[] fixed_array = new User[no_users];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    fixed_array[j++] = (User) head.value;
                    head = head.next;
                }
                i++;
            }
            return fixed_array;
        }

        /*
        * This method gets all the Users which fulfill the search criteria.
        * A new array is created of size no_users which is the number of users that have been added into the Hashmap.
        * A new array is created of size no_useres which is the number of users that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, the name of the temp variable is compared to the search query.
        * If this search query is met, it is added to the array and the next value from the list of users is compared.
        * The array with all the users that meet the search query are added and is then returned.
        */
        public User[] searchBox(String search) {
            User[] fixed_array = new User[no_users];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    User user = (User) head.value;
                    if (user.getName().toLowerCase().contains(search.toLowerCase())){
                        fixed_array[j++] = (User) head.value;
                    }
                    head = head.next;
                }
                i++;
            }
            return fixed_array;
        }

        /*
        * This method gets all the Users which fulfill the date criteria.
        * A new array is created of size no_users which is the number of users that have been added into the Hashmap.
        * A new array is created of size no_useres which is the number of users that have been added into the Hashmap.
        * A loop is created so that until i which is incremented by 1 doesn't reach the capacity of the hashmap, the head object is assigned
          to a temp varaible and until the end of the list is reached, the name of the temp variable is compared to the date given as a paramter.
        * If this Date joined of the user assigned to the temp user is before the date of the date given as a parameter, then this temp user is
          added to the array.
        * The array with all the users that match the date joined given as parameter are added and is then returned.
        */
        public User[] dateJoined(Date date) {
            User[] fixed_array = new User[no_users];
            int i = 0;
            int j = 0;
            while (i != size){
                dictionary<K,V> head = list[i];
                while (head != null){
                    User user = (User) head.value;
                    if (user.getDateJoined().before(date)){
                        fixed_array[j++] = (User) head.value;
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
    public void quickSort(User[] arr, int begin, int end) {
    if (begin < end) {
        int partitionIndex = partition(arr, begin, end);

        quickSort(arr, begin, partitionIndex-1);
        quickSort(arr, partitionIndex+1, end);
    }
    }

    /*
    * This method is used with the quicksort method.
    * The pivot of type User is assigned to the last element of the array given as a parameter,
    * Whilst the integer paramater begin is less than the end, if the element with a lower index has a date joined of after that than
      the date joined of the elemnt assigned to the pivot then the lower index element is assigned to a temp variable. Since the lower index element has a date joined of after the higher index element, these two elements switch index position so that these two dates are in order. This will loop until all elements are sorted by date. Once this occurs, the element in the middle is swapped out for the element at the end so that the list is fully sorted by date.
    */
    private int partition(User[] arr, int begin, int end) {
    User pivot = arr[end];
    int i = (begin-1);

    for (int j = begin; j < end; j++) {
        if (arr[j].getDateJoined().after(pivot.getDateJoined())) {
            i++;

            User swapTemp = arr[i];
            arr[i] = arr[j];
            arr[j] = swapTemp;
        }
    }

    User swapTemp = arr[i+1];
    arr[i+1] = arr[end];
    arr[end] = swapTemp;

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
    * This is a constructor for UserStore.
    * A new instance of a Hashmap is created which consists of a Key-Value pair and is assigned to the variable userInfo.
    */
    public UserStore() {
        userInfo = new HashMap<Integer, User>();
    }

    /*
    * This method is for add a user into the Hashmap.
    * The key entered into the hasmap is the Id associated with the user and the value is the User object given as a parameter.
    */
    public boolean addUser(User usr) {
        return userInfo.put(usr.getId(), usr);
    }

    /*
    * This method is to get the value associate with the User Id which is the key.
    */
    public User getUser(int uid) {
        return userInfo.get(uid);
    }

    /*
    * This method gets all the users.
    * This calls the method which gets all the users and clones all the users into a new array.
    * The array of the users is sorted by date with the latest first then returned.
    */
    public User[] getUsers() {
        User[] allUsers = userInfo.getallUsers().clone();
        quickSort(allUsers, 0, allUsers.length - 1);
        return allUsers;

    }

    /*
    * This method gets all the users which fulfill the search criteria.
    * This calls the method which gets all the users and clones all the users into a new array.
    * A new array is created which will store the users that meet the search criteria of size of all the users.
    * All the users that meet the criteria are put into an array.
    * All the users are sorted by date from latest to earliest and then returned.
    */
    public User[] getUsersContaining(String search) {
        User[] allUsers = userInfo.searchBox(search).clone();
        User[] meetsQuery = new User[getLength(allUsers)];
        for (int i = 0; i < getLength(allUsers); i++) {
            meetsQuery[i] = allUsers[i];
        }
        quickSort(meetsQuery, 0, getLength(allUsers)-1);
        return meetsQuery;
    }

    /*
    * This method gets all the users that joined before a given date.
    * This calls the method which gets all the users and clones all the users into a new array.
    * A new array is created which will store the users that have the date given as a parameter.
    * All the users that meet the criteria are put into an array.
    * All the users are sorted by date from latest to earliest and then returned.
    */
    public User[] getUsersJoinedBefore(Date date) {
        User[] allUsers = userInfo.dateJoined(date).clone();
        User[] meetsDate = new User[getLength(allUsers)];
        for (int i = 0; i < getLength(allUsers); i++) {
            meetsDate[i] = allUsers[i];
        }
        quickSort(meetsDate, 0, getLength(allUsers)-1);
        return meetsDate;
    }

}
