# Sorting-Competition-Materials-2026
Materials and results for the UMN Morris CSci 3501 sorting competition Fall 2026

# Table of contents
* [Goal of the competition](#goal)
* [The data](#data)
* [How is the data generated](#generating)
* [How do you need to sort the data](#sortingRules)
* [Setup for sorting](#setup)
* [Submision deadlines](#deadlines)
* [Scoring](#scoring)
* [Results of the final competition](#final)
* [Presentations](#presentation)


## Goal of the competition <a name="goal"></a>

The Sorting Competition is a multi-lab exercise on developing the fastest sorting algorithm for a given type of data. By "fast" we mean the actual running time and not the Big-Theta approximation. The solutions are developed in Java and will be ran on a single processor.

## The data  <a name="data"></a>

You are sorting binary strings that encode a numeric value in a rather unusual way. (details below)  

The data will be provided as a text file-- each line will contain a string of 0's and 1's.  The number of characters in each line will be produced using a custom random process (you can see the details in `DataGenerator.java`.  The basic idea is that the class variables `shortLen` and `longLen` will control the production of string lengths:

* Roughly 25% of the lengths will be less than `shortLen`.  Those lengths will never be less than 4 and cluster closer to `shortLen`
* Roughly 50% of the lengths will be between `shortLen` and `longLen`.  Those lengths will be selected at random uniformly
* Roughly 25% of the lengths will be longer than `longLen`.  Those lengths are produced using an exponential distribution whose rate parameter is related to `longLen - shortLen` (in other words-- the longer the distance between those two values, the longer the lengths you can expect to see from the "long side" of the distribution.

The output will be a text file of binary strings separated by newline characters.  

For example, using **much** smaller values of `shortLen` and `longLen` one might see the first few lines in the data file as

```
1101101
1101
100000
11111
    .
    .
    .
```

A good way to generate short data is to use

* `shortLen`: 5
* `longLen`: 8

For the competition I'll likely be generating 50,000 binary strings using the following parameters:

* `shortLen`: 500
* `longLen`: 1000


## The value of a string

Each binary string encodes a **set of distinct odd integers**.  The value of that string is the sum of those odd integers.  Each `1` in the string represents a different odd integer.  To determine that integer index (0-based) the locations in the string starting at the right-most end of the string.  If a `1` occurs at index $i$, then the encoded odd-integers is $2i+1$.  This is easiest to see with an example.  Let's consider the string "1011010".  It represents the odd integers 3, 7, 9, and 13 (see below) and thus has the value 3+7+9+13 = 32

```
Binary String || 1 | 0 | 1 | 1 | 0 | 1 | 0
        index || 6 | 5 | 4 | 3 | 2 | 1 | 0
  odd integer || 13| 11| 9 | 7 | 5 | 3 | 1
  ---------------------------------------------
        value || 13    + 9 + 7     + 3   = 32
```


## Comparing Sequences

Two binary strings $A$ and $B$ are ordered as follows

1. $A < B$ if the $\textrm{value}$ represented by $A$ is less than the $\textrm{value}$ represented by $B$
2. $A < B$ if the **number of ones** in $A$ is less than the **number of ones** in $B$
3. $A < B$ if the **length** of $A$ is less than the length of $B$
4. $A < B$ if $A$ is alphabetically before $B$

Note that this criteria are applied in the order given-- so don't proceed to criterion 2 unless the values of $A$ and $B$ are equal.


### Examples

Let's see this in action, the strings in the table below are used in `Group0.java` as tests.  We have calculated some key characteristics for their comparison:

String     | Calculation      | Value    | # of ones | Length
-----------|------------------|----------|-----------|--------
"101101"   | 1 + 5 + 7 + 11   |   24     | 4         | 6
"110011"   | 1 + 3 + 9 + 11   |   24     | 4         | 6
"10010000" | 9 + 15           |   24     | 2         | 8
"1100000"  | 11 + 13          |   24     | 2         | 7
"11111"    |1 + 3 + 5 + 7 + 9 |   25     | 5         | 5

#### Comparing 101101 to 11111

* First criterion is value.  
     * The value of "101101" is 24 
     * the value of "11111" is 25
* So "101101" < "11111"

#### Comparing 101101 to 10010000

* First criterion is value.  
     * The value of "101101" is 24 
     * the value of "10010000" is 24
     * Tied
* The second criterion is # of ones
     * "101101" has 4 ones
     * "10010000" has 2 ones
* So "10010000" < "101101"

#### Comparing 101101 to 110011

* First criterion is value.  
     * The value of "101101" is 24 
     * the value of "110011" is 24
     * Tied
* The second criterion is # of ones
     * "101101" has 4 ones
     * "110011" has 4 ones
     * Tied
* The third criterion is length
     * "101101" has length 6
     * "110011" has length 6
     * Tied
* The fourth criterion is alphabetical order
     * "101101" < "110011"  alphabetically
* So "101101" < "110011"

#### Comparing 10010000 to 1100000

* First criterion is value.       
     * the value of "10010000" is 24
     * the value of "1100000" is 24
     * Tied
* The second criterion is # of ones
     * "10010000" has 2 ones
     * "1100000" has 2 ones
     * Tied
* The third criterion is length:
     *"10010000" has length 8
     * "1100000" has length 7
* So "1100000" < "10010000" 

Please note that the binary strings your program will be processing for the competition will be much longer than these.

The file `SmallData.txt` will have a collection of shorter, human parsable examples so you can make sure everything is working correctly.

## Setup for sorting <a name="setup"></a>

The file [Group0.java](src/Group0.java) provides a template for the setup for your solution. Your class will be called `GroupN`, where `N` is the group number that is assigned to your group. The template class runs the sorting method once before the timing for the [JVM warmup](https://www.ibm.com/developerworks/library/j-jtp12214/index.html). It also pauses for 10ms before the actual test to let any leftover I/O or garbage collection to finish. Since the warmup and the actual sorting are done on the same array (for no reason other than simplicity), the array is cloned from the same input data. 

The data reading, the array cloning, the warmup sorting, and writing out the output are all outside of the timed portion of the method, and thus do not affect the total time. 

You may **not** use any **global variables** that **depend on your data**. You may, however, have global constants that are initialized to fixed values (no computation!) before the data is being read and stay the same throughout the run. These constants may be arrays of no more than 100 `long` numbers or equivalent amount of memory. For instance, if you are storing an array of objects that contain two `long` fields, you can only have 50 of them. 
We consider one `long` to be the same as two `int` numbers, so you can store an array of 200 `int` numbers. 
Strings use 16 bits (half of the size of an `int`) for *each* character and 16 bytes (128 bits) for the memory reference and other info for the string *itself*.  
If in doubt about specific cases, please discuss with me. 

The method in the [Group0.java](src/Group0.java) files that you may modify is the `sort` method. It must take an array of Strings (for example, `String []`). 
The method can sort in place (thus be `void`) or return type of the method can be what it is now or return the result in an array of another type.
If you are returning an array, the following rules have to be followed:
* Your `sort` method return type needs to be changed to whatever array you are returning. Consequently you would need to change the call in `main` to store the resulting array. 
* Your return type has to be an array (not an array list!) and it has to the length of the original array and contain the original values. 
For example, you may create your own class (I will call it `Data` as an example) that has the `String` value of a binary string, plus some other fields, perhaps the `int` value of the binary string. Then you will be returning an array of `Data`. 
You may not, however, create an array of just binary representations of all strings, return that array, and convert back to the original form for printing.   
* If you are returning a different type of an array, such as `Data`, you need to supply a method to write out your resulting array into a file. The method will access the `String` value field using a `get` method or directly (if it's accessible) and write it to a file. 
The file has to be exactly the same as in the prototype implementation; they will be compared using `diff` system command. 

If you are not changing the return type, you don't need to modify anything other than `sort` method and any methods/classes called from it. 

Even though you are not modifying anything other than the `sort` method, you still need to submit your entire class: copy the template, rename the Java class to your group number, and change the`sort` method. You may use supplementary classes, just don't forget to submit them. Make sure to add your names in comments when you submit. 

Your program must print **the only value**, which is the **time** (as it currently does). Any other printed output disqualifies your submission. If you use test prints, make sure to turn them off for submission. 

**Important:** if the sorting times may be too small to distinguish groups based on just one run of the sorting, so I may loop over the sorting section multiple times. If this is the case, I will let you know no later than a day after the preliminary competition and will modify `Group0` file accordingly.  

## Scoring <a name="scoring"></a>

The programs are tested on a few (between 1 and 3) data sets. For each data set each group's program is run three times, the median value counts. The groups are ordered by their median score for each data file and assigned places, from 1 to N. 

The final score is given by the sum of places for all data sets. If the sum of places is equal for two groups, the sum of median times for all the runs resolves the tie. So if one group was first for one data set and third for the other one (2 sets total being run), it scored better than a group that was third for the first data set and second for the other. However, if one group was first for the first set and third for the other one, and the second group was second in both, the sum of times determines which one of them won since the sum of places is the same (1 + 3 = 2 + 2). 

If a program has a compilation or a runtime error, doesn't sort correctly, or prints anything other than the total time in milliseconds, it gets a penalty of 1000000ms for that run. 

## System specs <a name="specs"></a>

The language used is Java that's installed in the CSci lab. It's ran on a single CPU core. 

I will post a script for running this program (with a correctness check and all), but for now a couple of things to know: run your program out of `/tmp` directory to avoid overhead of communications with the file server, and pin your program to a single core, i.e. run it like this:
``taskset -c 0 java GroupN``

##  Submision deadlines <a name="deadlines"></a>

The first preliminary competition will be run on Thursday October 29th in the lab. The purpose of it is mostly to check the correctness and to get a sense for your timing. 

The second preliminary round will be in the lab on Monday November 9th (due Sunday Nov 8th) 

The final competition will be on Thursday Dec 3rd in the lab (due Wednesday Dec 2nd)

Your Presentations of your algorithm will be Dec 10th.

The dates for other related assignments (code review and presentations) will be announced later. 

## Results <a name="final"></a>
Here is where the results of the final competition will go:

## Round 1 arguments

* `prelim1.txt`: 50,000 with `shortLen`= 500 and `longLen`=1000
* `prelim1.txt`: 60,000 with `shortLen`= 500 and `longLen`=1000

## Round 2 arguments

* `prelim1.txt`: TBD
* `prelim2.txt`: TBD

