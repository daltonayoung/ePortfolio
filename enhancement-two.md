---
layout: page
title: "Enhancement Two: Algorithms & Data Structures"
permalink: /enhancement-two.html
---

# Enhancement Two: Algorithms & Data Structures

<a href="https://github.com/maxpayne777/ePortfolio/tree/main/Enhancement%201%20-%20Software%20Design%20and%20Engineering" target="_blank" rel="noopener noreferrer">View the code before this enhancement (Enhancement One)</a> &nbsp;|&nbsp; <a href="https://github.com/maxpayne777/ePortfolio/tree/main/Enhancement%202%20-%20Algorithms%20and%20Data%20Structures" target="_blank" rel="noopener noreferrer">View the enhanced source code and commit history</a>

## Describing the Artifact

The artifact is the Contact Service, the same Java application I originally built in CS-320 (Software Testing, Automation, and Quality Assurance) that Enhancement One turned into a working command-line application. Enhancement Two starts from Enhancement One's finished state. Enhancement One has a ContactService class storing Contact objects in a single ArrayList, backed by a menu-driven ContactApp CLI for adding, listing, searching, updating, and deleting contacts. Every lookup, insert, and delete in that version still touched the list one entry at a time, an O(n) scan regardless of which contact was needed, and listAll sorted a fresh copy of the whole list on every call, O(n log n) just to display it.

## Justifying Its Inclusion

I picked this artifact for the algorithms and data structures category because its operations were all built around a single linear structure, O(n) regardless of what each one actually needed to do. Looking up a contact by ID, keeping the list sorted for display, and searching by name are three different access patterns, and a single ArrayList can't serve all three well at once.

I added a HashMap keyed by contact ID alongside the existing list, so getContact, updatePhoneNumber, and updateAddress now run in O(1), without scanning for a match. The four update methods also used to each repeat their own lookup-and-throw logic before this change. Now they all delegate to getContact and call the setter directly, so that logic exists in one place instead of four. I kept a second structure, an ArrayList kept sorted by last name and then first name at all times, maintained incrementally through insertIntoSorted and removeFromSorted helpers that use Collections.binarySearch() to find the correct position in O(log n), without resorting the whole list. listAll became a direct O(1) return of that already-sorted list. Adding, deleting, or renaming a contact still costs O(n) overall. The binary search step is O(log n), but shifting every element after the insertion or removal point in the ArrayList is what keeps the total linear.

Renaming a contact also has to keep contactsByName correct even if the new name gets rejected. updateFirstName and updateLastName remove the contact from contactsByName using its old name first, then attempt the actual rename, with the reinsert happening in a finally block regardless of whether the setter succeeds or throws. Without that finally block, a rejected rename would leave the contact removed from contactsByName but never put back, permanently missing from search and listAll even though it still exists in contactsById.

searchByName needed the most rework. It used to scan every contact's full name for a case-insensitive substring, checking every contact in the list regardless of how many there were. The piece that actually makes it work with binarySearch() is the comparator: instead of requiring an exact match, it treats a contact as a match if its last name starts with the query prefix. Everything sharing a prefix sorts contiguously in a list ordered by last name, the same reason exact-match duplicates sit next to each other, so binarySearch() can land anywhere inside that block using this comparator. From that landing point, a short walk outward in both directions, stopping as soon as a neighbor's last name no longer starts with the prefix, collects every match. For example, searching "You" this way returns both "Young" and "Younger" in O(log n + k) time. An arbitrary substring match anywhere in a name doesn't share that contiguity, so it isn't something binarySearch() can accelerate, which is why the search narrowed to last-name prefixes specifically.

The table below summarizes the cost of every ContactService operation after this enhancement.

| Operation | Overall Cost |
|---|---|
| getContact(id) | O(1) |
| updatePhoneNumber / updateAddress | O(1), shared object reference, sort key untouched |
| updateFirstName / updateLastName | O(n), sort key changed, requires repositioning in the sorted list |
| addContact | O(n), ArrayList shift on insert |
| deleteContact | O(n), ArrayList shift on removal |
| listAll | O(1), the list is already sorted |
| searchByName | O(log n + k), binary search into the matching block, then expand to collect k total matches (degrades to O(n) if nearly every contact shares the prefix) |

## Course Outcomes

I met Outcome 3, algorithmic principles and design trade-offs, by matching each structure to the access pattern it actually serves: a HashMap for ID-based access and a sorted ArrayList for ordered listing and prefix search. I also reasoned precisely about the cost of each operation, summarized in the table above.

I met Outcome 4, well-founded and innovative tools and techniques, by using Collections.binarySearch() correctly against a comparator purpose-built for prefix matching. Module One's original pseudocode only called for a plain last-name search, so building it as a prefix match instead is the one real update to my outcome-coverage plan from this enhancement. I also continued writing new JUnit tests for this enhancement. While running them with coverage, the same way I did in Enhancement One, I found a few paths in the code that weren't covered by the tests and was able to fix those.

I kept building evidence toward Outcome 1 as well, through Javadoc on every new or changed method and commits staged the same way as Enhancement One, each explaining why a change was made. The commits for Enhancement Two can be viewed <a href="https://github.com/maxpayne777/ePortfolio/commits/main/Enhancement%202%20-%20Algorithms%20and%20Data%20Structures" target="_blank" rel="noopener noreferrer">here</a>.

## Reflecting on the Process

The biggest challenge in this enhancement was figuring out what searchByName should actually do once it could no longer just scan everything. My first instinct was to add a third structure, a second sorted list keyed by first name, so I could keep supporting Enhancement One's broader full-name search on top of a real binary search. That would have worked, but it also would have meant maintaining three structures in sync, which would have gotten too messy and was really out of scope for the original plan. The original design was always a last-name search over a single sorted structure, and Enhancement One's substring match was a stand-in built before that structure existed, not a requirement I needed to preserve.

I caught one consistency bug during development. The prefix-matching comparator I wrote for searchByName was case-insensitive, but the comparator that actually keeps the list sorted was not. A case-insensitive search against a case-sensitive sort order is not reliable, since binary search only works correctly if the comparator used to search agrees with how the list is actually ordered. I made the sort comparator case-insensitive too to resolve the issue.

Running tests on the code still revealed some missing test cases, which taught me that passing tests and full coverage are not the same thing. After removeFromSorted was written and tested, a coverage check showed its backward-walk step, the loop that steps back through a block of same-named contacts to find the exact one being deleted, had never actually executed. Two duplicate contacts were always enough to trigger the deletion logic but not enough to force that particular loop to run more than zero times, since binary search happened to land on the earlier of the two. Adding a third duplicate name to the test forced the walk to actually happen. The same pattern showed up again in searchByName's forward-expansion loop, which every existing test had only ever exited by reaching the end of the contact list. I added a test case with a non-matching contact sorted directly after the matching block, so the loop stopped because of a real comparison instead of running out of list. Coverage also caught two guard-clause paths in searchByName that none of my early tests exercised: a null prefix and a prefix longer than any valid last name. Both are supposed to return an empty list immediately without searching, but nothing had actually called searchByName with either one until coverage pointed it out.
