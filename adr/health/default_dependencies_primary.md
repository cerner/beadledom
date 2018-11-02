## **Title**: Dependencies Default to Primary

## **Status**: accepted

## **Context**:

Primary Health checks were not checking if dependencies were primary or not before checking their health.
This was not the intended behavior of the primary health check endpoint, as it should only check the primary dependencies of any given project.

## **Decision**:

In order to do this passively, we changed the dependency class to default to primary unless otherwise specified.
We did this because we don't know if our consumers rely on the previous behavior of meta/health checking dependencies if they were of unspecified importance.
 
## **Consequences**:

Any system that wants a dependency to be secondary must explicitly state it as such.

