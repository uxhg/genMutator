instructions: list = [
    "Analyse the following codes and tell if there is performance issue or not.",
    "Examine the provided code and assess whether it exhibits performance issues.",
    # "Analyze the code snippets below to determine if any performance concerns exist.",
    "Evaluate the given Java code for any signs of performance bottlenecks.",
    "Review the code examples to check for any code performance issues.",
    "Please inspect the following code snippet and identify any potential performance issues.",
    # "Could you analyze the provided code and determine if there are opportunities to improve performance?"
]

output_type_vi: list = [
    "Yes. The search criteria involves nullable attributes, please ensure fetching null values is intended.",
    # "Indeed, a potential performance problem is present, likely stemming from nullable attribute usage. Verify the intent of fetching null values."
    "Yes, there is a performance concern regarding nullable attributes. Please clarify if null value retrieval is intentional.",
    "Yes, there is a risk of decreased performance as the search involves nullable attributes. Adding a null check to the criteria would prevent unnecessary records from being returned.",
    "Yes, the search criteria does not exclude null values, which may result in unnecessary records being returned. Adding a null check to the criteria could improve performance.",
    # "The search is not restricted by null values, so there could be unintended overfetching. I would recommend explicitly excluding nulls in the criteria to optimize the query."
    "Yes, the search includes nullable fields which can lead to full table scans if null values are fetched unintentionally. Consider adding explicit checks for null.",
]

output_type_i: list = [
    "Yes, there is a potential performance issue. Fetching the full entity to check existence can be avoided by using an exists query instead.",
    "Yes, the code could be optimized by using an existence check to verify presence rather than retrieving the full object from the database.",
    "Yes, retrieving the entity to check if it exists introduces unnecessary overhead. An exists check would improve performance by avoiding the fetch.",
    "Yes, a performance concern is evident: Consider replacing full entity retrieval for existence verification with an exists query for enhanced efficiency.",
    "Yes, performance optimization potential identified: swap out the full entity retrieval for an exists query to potentially boost efficiency"
]

output_type_iii: list = [
    "Yes, retrieving the full entity to only get an attribute introduces unnecessary overhead. Consider using a query that selects just the needed attribute instead.",
    # "Yes, instead of fetching the entire entity, retrieve only the attribute needed through a query. This will improve performance by avoiding fetching unused data.",
    "Yes, there is an opportunity for optimization. Fetching the complete entity is not necessary; instead, retrieve the required attribute directly for improved efficiency.",
    "Yes, a performance enhancement suggestion: Instead of retrieving the complete entity, tailor the query to fetch only the essential attribute. This strategy avoids unnecessary data retrieval and boosts efficiency.",
    "Yes, to enhance performance, retrieve solely the necessary attribute through a refined query, avoiding the overhead of fetching surplus data.",
    "Yes, consider optimizing by querying only the specific attributes required, rather than fetching the entire entity. This targeted approach will notably enhance performance by minimizing unnecessary data retrieval.",
]
