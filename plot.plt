set style line 1 lt 1 lw 1 lc rgb "red"
set style line 2 lt 2 lw 1 lc rgb "blue"
set style line 3 lt 3 lw 1 lc rgb "green"
set xlabel "Num. variabili"
set ylabel "Tempo (ms)"
set grid
set key left top
#set key at 0,0 
plot "./arc time.txt" with line ls 1 title "arcConsistency","./bounds time.txt" title "boundsConsistency" with line ls 2
#plot "./range time.txt" with line ls 3
