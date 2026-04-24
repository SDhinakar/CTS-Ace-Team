SELECT employee_id, name, department, salary
FROM (
    SELECT *,
           DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS rnk
    FROM employees
) t
WHERE rnk <= 3;
