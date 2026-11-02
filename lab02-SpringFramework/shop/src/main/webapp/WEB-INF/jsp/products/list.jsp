<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products Inventory</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #4f46e5;
            --primary-hover: #4338ca;
            --primary-light: #eef2ff;
            --success: #10b981;
            --success-light: #ecfdf5;
            --danger: #ef4444;
            --danger-light: #fef2f2;
            --warning: #f59e0b;
            --warning-light: #fffbeb;
            --slate-50: #f8fafc;
            --slate-100: #f1f5f9;
            --slate-200: #e2e8f0;
            --slate-300: #cbd5e1;
            --slate-400: #94a3b8;
            --slate-600: #475569;
            --slate-700: #334155;
            --slate-800: #1e293b;
            --slate-900: #0f172a;
            --radius-sm: 6px;
            --radius-md: 10px;
            --radius-lg: 16px;
            --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
            --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
            --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Plus Jakarta Sans', system-ui, -apple-system, sans-serif;
            background-color: #f8fafc;
            color: var(--slate-800);
            line-height: 1.5;
            padding: 2.5rem 1.5rem;
            min-height: 100vh;
        }

        .container {
            max-width: 1100px;
            margin: 0 auto;
        }

        /* Top Header */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 1rem;
            margin-bottom: 2rem;
        }

        .header-title-group h1 {
            font-size: 1.875rem;
            font-weight: 700;
            color: var(--slate-900);
            letter-spacing: -0.025em;
            display: flex;
            align-items: center;
            gap: 0.625rem;
        }

        .currency-badge {
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            padding: 0.25rem 0.625rem;
            background-color: var(--primary-light);
            color: var(--primary);
            border-radius: 9999px;
            letter-spacing: 0.05em;
        }

        .header-subtitle {
            color: var(--slate-600);
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }

        /* Flash Message Alert */
        .flash-card {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 1rem 1.25rem;
            background-color: var(--success-light);
            border: 1px solid #bbf7d0;
            color: #166534;
            border-radius: var(--radius-md);
            margin-bottom: 1.5rem;
            box-shadow: var(--shadow-sm);
            animation: slideDown 0.3s ease-out;
        }

        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-8px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .flash-card svg {
            width: 1.25rem;
            height: 1.25rem;
            flex-shrink: 0;
            color: var(--success);
        }

        /* Quick Stat Cards */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 1rem;
            margin-bottom: 1.75rem;
        }

        .stat-card {
            background: #ffffff;
            padding: 1.25rem;
            border-radius: var(--radius-md);
            border: 1px solid var(--slate-200);
            box-shadow: var(--shadow-sm);
        }

        .stat-label {
            font-size: 0.75rem;
            font-weight: 600;
            color: var(--slate-400);
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .stat-value {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--slate-900);
            margin-top: 0.25rem;
        }

        .stat-hint {
            font-size: 0.75rem;
            color: var(--slate-600);
            margin-top: 0.25rem;
        }

        /* Control Bar (Search + Action) */
        .control-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 1rem;
            flex-wrap: wrap;
            margin-bottom: 1.25rem;
        }

        .search-form {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            flex: 1;
            max-width: 440px;
        }

        .search-input-wrapper {
            position: relative;
            flex: 1;
        }

        .search-input-wrapper svg {
            position: absolute;
            left: 0.875rem;
            top: 50%;
            transform: translateY(-50%);
            width: 1rem;
            height: 1rem;
            color: var(--slate-400);
            pointer-events: none;
        }

        .search-input {
            width: 100%;
            padding: 0.625rem 0.875rem 0.625rem 2.375rem;
            font-size: 0.875rem;
            font-family: inherit;
            border: 1px solid var(--slate-300);
            border-radius: var(--radius-md);
            background-color: #ffffff;
            color: var(--slate-800);
            transition: all 0.2s ease;
        }

        .search-input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            padding: 0.625rem 1.125rem;
            font-size: 0.875rem;
            font-weight: 600;
            font-family: inherit;
            border-radius: var(--radius-md);
            cursor: pointer;
            transition: all 0.15s ease-in-out;
            text-decoration: none;
            border: none;
        }

        .btn-primary {
            background-color: var(--primary);
            color: #ffffff;
            box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
        }

        .btn-primary:hover {
            background-color: var(--primary-hover);
            transform: translateY(-1px);
            box-shadow: var(--shadow-md);
        }

        .btn-secondary {
            background-color: #ffffff;
            color: var(--slate-700);
            border: 1px solid var(--slate-300);
        }

        .btn-secondary:hover {
            background-color: var(--slate-50);
            border-color: var(--slate-400);
        }

        .btn-clear {
            font-size: 0.8125rem;
            color: var(--slate-600);
            text-decoration: none;
            padding: 0.5rem;
        }

        .btn-clear:hover {
            color: var(--danger);
        }

        /* Table Card Container */
        .table-card {
            background: #ffffff;
            border-radius: var(--radius-lg);
            border: 1px solid var(--slate-200);
            box-shadow: var(--shadow-sm);
            overflow: hidden;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
            font-size: 0.875rem;
        }

        thead {
            background-color: var(--slate-50);
            border-bottom: 1px solid var(--slate-200);
        }

        th {
            padding: 0.875rem 1.25rem;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            color: var(--slate-600);
        }

        th.num, td.num {
            text-align: right;
            font-variant-numeric: tabular-nums;
        }

        tbody tr {
            border-bottom: 1px solid var(--slate-100);
            transition: background-color 0.15s ease;
        }

        tbody tr:last-child {
            border-bottom: none;
        }

        tbody tr:hover {
            background-color: #fafbfc;
        }

        td {
            padding: 1rem 1.25rem;
            color: var(--slate-700);
            vertical-align: middle;
        }

        .product-name {
            font-weight: 600;
            color: var(--slate-900);
        }

        .product-id {
            font-size: 0.75rem;
            color: var(--slate-400);
            margin-right: 0.35rem;
        }

        .price-text {
            font-weight: 500;
            color: var(--slate-800);
        }

        .gross-price {
            font-weight: 600;
            color: var(--primary);
        }

        /* Stock Status Pill Badge */
        .stock-pill {
            display: inline-flex;
            align-items: center;
            gap: 0.375rem;
            padding: 0.25rem 0.625rem;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 600;
        }

        .stock-pill.normal {
            background-color: var(--success-light);
            color: #065f46;
        }

        .stock-pill.normal::before {
            content: "";
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background-color: var(--success);
        }

        .stock-pill.low-alert {
            background-color: var(--danger-light);
            color: #991b1b;
            border: 1px solid #fecaca;
        }

        .stock-pill.low-alert::before {
            content: "";
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background-color: var(--danger);
        }

        /* Backward-compatible class for tests */
        tr.low {
            background-color: #fff9f9;
        }

        /* Empty State */
        .empty-state {
            padding: 4rem 2rem;
            text-align: center;
            color: var(--slate-400);
        }

        .empty-state svg {
            width: 3rem;
            height: 3rem;
            margin-bottom: 0.75rem;
            color: var(--slate-300);
        }

        .empty-state h3 {
            font-size: 1.125rem;
            font-weight: 600;
            color: var(--slate-700);
            margin-bottom: 0.25rem;
        }

        .empty-state p {
            font-size: 0.875rem;
            color: var(--slate-400);
        }
    </style>
</head>
<body>

<div class="container">

    <!-- Top Header -->
    <div class="page-header">
        <div class="header-title-group">
            <h1>
                Products Inventory
                <span class="currency-badge"><c:out value="${currency}"/></span>
            </h1>
            <p class="header-subtitle">Server-rendered catalog with dynamic taxation & inventory thresholds</p>
        </div>
        <a href="/products/new" class="btn btn-primary">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            Add New Product
        </a>
    </div>

    <!-- Flash Message -->
    <c:if test="${not empty message}">
        <div class="flash-card">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            <span><c:out value="${message}"/></span>
        </div>
    </c:if>

    <!-- Quick Stats -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Total Listed Products</div>
            <div class="stat-value"><c:out value="${products.size()}"/></div>
            <div class="stat-hint">Active items in catalog</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Currency</div>
            <div class="stat-value"><c:out value="${currency}"/></div>
            <div class="stat-hint">Configured via ShopProperties</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Low Stock Threshold</div>
            <div class="stat-value"><c:out value="${threshold}"/> units</div>
            <div class="stat-hint">Triggers HealthIndicator warning</div>
        </div>
    </div>

    <!-- Control Bar (Search Form) -->
    <div class="control-bar">
        <form method="get" action="/products" class="search-form">
            <div class="search-input-wrapper">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                <input type="text" name="q" class="search-input" value='<c:out value="${q}"/>' placeholder="Search products by name...">
            </div>
            <button type="submit" class="btn btn-secondary">Search</button>
            <c:if test="${not empty q}">
                <a href="/products" class="btn-clear">Reset</a>
            </c:if>
        </form>
    </div>

    <!-- Product Table Card -->
    <div class="table-card">
        <table>
            <thead>
                <tr>
                    <th>Product</th>
                    <th class="num">Net Price</th>
                    <th class="num">Gross Price</th>
                    <th class="num">Stock Status</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty products}">
                        <tr>
                            <td colspan="4">
                                <div class="empty-state">
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
                                    <h3>No products found</h3>
                                    <p>Try searching for a different keyword or create a new product.</p>
                                </div>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="p" items="${products}">
                            <tr class="${p.stock < threshold ? 'low' : ''}">
                                <td>
                                    <span class="product-id">#${p.id}</span>
                                    <span class="product-name"><c:out value="${p.name}"/></span>
                                </td>
                                <td class="num price-text">
                                    <fmt:formatNumber value="${p.price}" minFractionDigits="2"/>
                                </td>
                                <td class="num gross-price">
                                    <fmt:formatNumber value="${p.gross}" minFractionDigits="2"/>
                                </td>
                                <td class="num">
                                    <c:choose>
                                        <c:when test="${p.stock < threshold}">
                                            <span class="stock-pill low-alert">${p.stock} (Low)</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="stock-pill normal">${p.stock} in stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>
