# 分区排序

## 问题概述
- 数据块是内存中的有序数组
- 分区是数据块的有序排列
- 需要对 X 个分区进行全局排序

## 思路
因为分区内的数据块有序，因此整个分区可以当作一个有序数组，问题转换为归并排序。

因为数据本身在内存中，因此可以使用内排序，**不必做多路归并**。

#### 基本思路
数组两两归并，归并后的数组再做两两归并，直到最终只有一个数组。

归并过程：
1. 两个数组分别取出第一个数，比较大小，更小的值输出；
2. 然后从该值所在的数组里取下一个数；
3. 重复该过程，直到两个数组全部取完。

#### 多线程优化
1. 把每个数组看作一个任务，放到任务队列中。
2. 每个线程既是消费者，也是生产者。每次消费 2 个数组，生产 1 个数组。
3. 每次消费从队头取数组，生产时从队尾压数组，这样保证先消费较短的数组。
4. 当队列只剩 1 个数组，且没有正在进行的任务时，结束。

#### 复杂度
令分区数有 K 个，总共的数据有 N 条。

平均时间复杂度为 O(N * logK)，空闲复杂度为 O(N)。

## 代码结构
Block - 数据块的定义

Partition - 分区的定义

MergeSortJob - 描述一个归并任务

MergeSortThreadPoolExecutor - 线程池 & 任务队列

MergeSortK - 算法入口

TestCase - 测试用例
