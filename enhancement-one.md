---
layout: page
title: "Enhancement One: Software Design & Engineering"
permalink: /enhancement-one.html
---

# Enhancement One: Software Design & Engineering

[View the original, pre-enhancement artifact](https://github.com/maxpayne777/ePortfolio/tree/main/CS-320%20Original%20Artifact) &nbsp;|&nbsp; [View the enhanced source code and commit history](https://github.com/maxpayne777/ePortfolio/tree/main/Enhancement%201%20-%20Software%20Design%20and%20Engineering)

## Describing the Artifact

The artifact is the Contact Service, a Java application I originally built in CS-320 (Software Testing, Automation, and Quality Assurance) during the Summer 2021 term. It has a Contact class that holds a contact's id, first name, last name, phone number, and address, and validates each of those fields when the object is created. A ContactService class adds, deletes, updates, and retrieves contacts from an in-memory list. The original version had no user interface. It was only ever exercised through 37 JUnit tests, and there was no main method anywhere in the project.

## Justifying Its Inclusion

I picked this artifact for the software design and engineering category because it had a real, fixable design flaw and no way for anyone to actually use it. The original addContact method required the caller to already know a contact's ID before adding one, which is the same problem as letting a database client choose its own primary key. A real user would have no way to know what ID would be safe to type.

This enhancement demonstrates a few specific skills. I corrected the leaky API by adding an addContact overload that generates a contact's ID internally, so a user never has to supply one themselves. I kept the original method in place too, since the existing test suite still calls it directly and it's still a reasonable lower-level option for a caller that already has a full Contact object.

I also kept the interface separate from the service. The CLI lives in its own class, ContactApp, and calls into the existing ContactService without touching its internals. Building it also meant adding listAll and searchByName to ContactService itself, since the plan's CLI pseudocode called for both but the original artifact never had them.

The CLI catches invalid input at the point where a person actually interacts with the program and shows a clear message instead of crashing. I also had it validate each field individually as it's entered, so a mistake on the first field gets caught right away instead of after the whole form has been filled out. To do that without duplicating any validation logic, I used a throwaway Contact object as a scratchpad and applied each entered value through its real setter, using Java's Consumer functional interface and method references. That way the validation rule itself only exists in one place.

Altogether, the artifact went from a class that only JUnit tests could touch to a working menu-driven CLI that supports adding, listing, searching, updating, and deleting contacts, all on top of the same validation the original artifact already had.

## Course Outcomes

The Module One plan mapped this enhancement to Outcomes 3, 4, and 1. I met Outcome 3, algorithmic principles and design trade-offs, by working through the ID-generation redesign and by deciding where the line between the CLI and the service layer should sit. Both required weighing real trade-offs instead of just picking whatever was fastest to write.

I met Outcome 4, well-founded and innovative tools and techniques, more thoroughly than I originally planned. The original plan only scoped test-coverage evidence for Enhancements 2 and 3. While building this enhancement, I ran a code coverage tool against my own tests and found a branch that had never actually run, then wrote a test to close it. That is a new update to my outcome-coverage plan and everything else was met as planned.

I met Outcome 1, collaborative environments, through Javadoc on every new or changed method and through commits staged with messages that explain why a change was made, not just what changed. The commits for Enhancement 1 can be viewed [here](https://github.com/maxpayne777/ePortfolio/commits/main/Enhancement%201%20-%20Software%20Design%20and%20Engineering).

## Reflecting on the Process

The biggest thing I learned is that reasoning about code and actually running it turn up different problems. I caught one design flaw myself before I ever committed anything just by thinking it through. searchByName originally returned only the first matching contact, so a second contact with the same full name would have been impossible to find through search. I found a second, more basic problem only after I had the CLI working and actually used it. I added two contacts named Dalton and searched "Dalton" expecting to find both, and got nothing back, because the search required an exact full-name match instead of a partial one. Nothing about reading the code ahead of time had shown me that issue.

searchByName and its tests were new work for this enhancement, not something already established that I came back to later. After writing the method and a first round of tests for it and confirming they all passed, I ran the JUnit tests with coverage as a next verification step before committing. It reported less than full coverage on ContactService, even though every test passed. Looking into it, every one of my searchByName tests either searched an empty contact list or a list where every contact matched the query. The case where an existing, non-matching contact needs to be correctly excluded had never actually run. I added a test for that case, which closed the gap.

The biggest challenge was deciding what not to fix. Partway through, I noticed the address field's 30-character limit is too short for most real addresses. I was tempted to fix it, but doing so would have meant rewriting parts of the original 37 tests. Those tests were built specifically around that 30-character limit, and address length wasn't something any of the three planned enhancements was actually meant to address. I decided to leave it alone and just note it as a limitation I noticed but chose not to fix.
