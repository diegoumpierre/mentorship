# Solution

(7) Build Ticket system, where you should be able 

- to sell x number of tickets per different shows 
- choose the seat number, 
- zone of the venue, 
- date and respect 
- maximum capacity.

 The Principal problem here is the thing in concurrency

## TODO

- [x] set up persistence — h2
- [x] implement `buyTicket` in `ShowServiceImpl`
- [x] make `listAllShow` return real data 
- [ ] finish `reserveASeat` — reservation TTL
- [ ] controller endpoints (list shows, pick a seat, buy)
- [ ] concurrency 
  - [ ] Need to pick between pessimistic locking
  - [ ] optimistic locking with @Version
  - [ ] Can't let two people buy the same seat
- [ ] validate the venue's max capacity before selling
- [ ] Tests for all
