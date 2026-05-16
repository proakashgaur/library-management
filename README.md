# All books
GET http://localhost:8080/api/v1/books

# Search
GET http://localhost:8080/api/v1/books?search=spring

# Issue a book (use IDs from GET responses)
POST http://localhost:8080/api/v1/issues
{"bookId": "...", "memberId": "..."}

# Return it
PATCH http://localhost:8080/api/v1/issues/{issueId}/return

# Dashboard stats
GET http://localhost:8080/api/v1/dashboard/stats



crete this
