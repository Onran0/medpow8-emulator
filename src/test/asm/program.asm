dial 0
dial 1

mov 0, r0
mov 1, r1

jmp 6
hlt

mov r1, r2
add r0, r1
mov r2, r0

dial r1

cmp r1, 233

je 5
jmp 6