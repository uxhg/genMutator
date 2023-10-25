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
    "Affirmative, there is a performance concern regarding nullable attributes. Please clarify if null value retrieval is intentional.",
    "There is a risk of decreased performance as the search involves nullable attributes. Adding a null check to the criteria would prevent unnecessary records from being returned.",
    "The search criteria does not exclude null values, which may result in unnecessary records being returned. Adding a null check to the criteria could improve performance.",
    # "The search is not restricted by null values, so there could be unintended overfetching. I would recommend explicitly excluding nulls in the criteria to optimize the query."
    "Yes, the search includes nullable fields which can lead to full table scans if null values are fetched unintentionally. Consider adding explicit checks for null.",
]
