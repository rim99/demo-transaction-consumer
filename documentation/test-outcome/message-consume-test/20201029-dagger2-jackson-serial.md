
## index 1: message consume latency

Ramp-up time |   Hotspot     | OpenJ9
 :--:        |    :--:       | :--:  
Average      |   5 min       |  0
P99          |   6 min       |  0
P999         |   3min 15sec  |  1min 45sec

10 min after ramped-up  |   Hotspot     | OpenJ9
 :--:                   |    :--:       | :--:  
Average                 |   39ms        |  38ms
P99                     |   110ms       |  75ms
P999                    |   240ms       |  250ms

5 min before end   |   Hotspot               | OpenJ9
 :--:              |    :--:                 | :--:  
Average            |   34ms                  |  37ms
P99                |   70ms                  |  130ms
P999               |   110ms (230ms mostly)  |  240ms


## index 2: memory usage

usedHeap | Hotspot   | OpenJ9
 :--:    | :--:      | :--:
max      | 31.8 MB   | 20.0 MB 
min      | 13.5 MB   | 11.1 MB


GC count | Hotspot   | OpenJ9
 :--:    | :--:      | :--:
minor    | 500       | 1902
major    | 0         | 22 

GC time  | Hotspot   | OpenJ9
 :--:    | :--:      | :--:
minor    | 3.6s      | 6.92s 
major    | 0         | 0.28s
