# CTS ACE Team Submission

## Overview
This repository contains solutions to two distinct problem statements implemented using Java and SQL. The goal is to demonstrate both algorithmic problem-solving skills and database query optimization using industry-standard approaches.

---

## 🔹 Problem 1: Longest Consecutive Sequence (Java)

### Problem Statement
Given an unsorted array of integers, find the length of the longest consecutive elements sequence.

### Approach
- Used a HashSet to store elements for constant-time lookup.
- Identified the starting point of sequences by checking if the previous element exists.
- Iteratively counted sequence length.
- Avoided sorting to achieve O(n) time complexity.

### Key Highlights
- Time Complexity: O(n)
- Space Complexity: O(n)
- Efficient handling of duplicates and unordered data

---

## 🔹 Problem 2: Top 3 Salaries per Department (SQL)

### Problem Statement
Retrieve the top three highest-paid employees from each department.

### Approach
- Used `DENSE_RANK()` window function.
- Partitioned data by department.
- Sorted salaries in descending order.
- Filtered results to top 3 ranks per department.

### Key Highlights
- Uses advanced SQL window functions
- Handles duplicate salaries effectively
- Optimized for large datasets

---
## 🚀 Technologies Used
- Java
- SQL (Window Functions)

---

## 📌 Conclusion
These solutions demonstrate efficient problem-solving using optimized algorithms and modern SQL techniques. The focus was on scalability, performance, and clean implementation aligned with real-world development practices.

---
