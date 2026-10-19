<%@ page contentType="text/html;charset=UTF-8" %> <%@ taglib prefix="c"
uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>New Product</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap"
      rel="stylesheet"
    />
    <style>
      :root {
        --primary: #4f46e5;
        --primary-hover: #4338ca;
        --primary-light: #eef2ff;
        --danger: #ef4444;
        --danger-light: #fef2f2;
        --danger-border: #fecaca;
        --slate-50: #f8fafc;
        --slate-100: #f1f5f9;
        --slate-200: #e2e8f0;
        --slate-300: #cbd5e1;
        --slate-400: #94a3b8;
        --slate-600: #475569;
        --slate-700: #334155;
        --slate-800: #1e293b;
        --slate-900: #0f172a;
        --radius-md: 10px;
        --radius-lg: 16px;
        --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
        --shadow-md:
          0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
      }

      * {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
      }

      body {
        font-family:
          "Plus Jakarta Sans",
          system-ui,
          -apple-system,
          sans-serif;
        background-color: #f8fafc;
        color: var(--slate-800);
        line-height: 1.5;
        padding: 3rem 1.5rem;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .form-container {
        width: 100%;
        max-width: 520px;
      }

      .back-link {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        color: var(--slate-600);
        font-size: 0.875rem;
        font-weight: 500;
        text-decoration: none;
        margin-bottom: 1.25rem;
        transition: color 0.15s ease;
      }

      .back-link:hover {
        color: var(--primary);
      }

      .card {
        background: #ffffff;
        border-radius: var(--radius-lg);
        border: 1px solid var(--slate-200);
        box-shadow: var(--shadow-md);
        padding: 2.25rem;
      }

      .card-header {
        margin-bottom: 1.75rem;
      }

      .card-header h1 {
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--slate-900);
        letter-spacing: -0.025em;
      }

      .card-header p {
        font-size: 0.875rem;
        color: var(--slate-600);
        margin-top: 0.25rem;
      }

      /* Error Summary Box */
      .error-summary {
        background-color: var(--danger-light);
        border: 1px solid var(--danger-border);
        border-radius: var(--radius-md);
        padding: 1rem 1.25rem;
        margin-bottom: 1.5rem;
      }

      .error-summary-title {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        font-weight: 600;
        color: #991b1b;
        margin-bottom: 0.375rem;
      }

      .error-summary-title svg {
        width: 1rem;
        height: 1rem;
        flex-shrink: 0;
        color: var(--danger);
      }

      .error-list {
        margin-left: 1.5rem;
        font-size: 0.8125rem;
        color: #b91c1c;
      }

      /* Form Controls */
      .form-group {
        margin-bottom: 1.25rem;
      }

      label {
        display: block;
        font-size: 0.875rem;
        font-weight: 600;
        color: var(--slate-700);
        margin-bottom: 0.375rem;
      }

      .input-wrapper {
        position: relative;
      }

      input {
        width: 100%;
        padding: 0.6875rem 0.875rem;
        font-size: 0.9375rem;
        font-family: inherit;
        border: 1px solid var(--slate-300);
        border-radius: var(--radius-md);
        background-color: #ffffff;
        color: var(--slate-900);
        transition: all 0.2s ease;
      }

      input:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
      }

      .field-hint {
        font-size: 0.75rem;
        color: var(--slate-400);
        margin-top: 0.375rem;
      }

      .error {
        color: var(--danger);
        font-size: 0.8125rem;
        margin-top: 0.375rem;
        font-weight: 500;
      }

      /* Action Buttons */
      .actions-group {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-top: 2rem;
        padding-top: 1.25rem;
        border-top: 1px solid var(--slate-100);
      }

      .btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0.6875rem 1.25rem;
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
        flex: 1;
        box-shadow: var(--shadow-sm);
      }

      .btn-primary:hover {
        background-color: var(--primary-hover);
        transform: translateY(-1px);
        box-shadow: var(--shadow-md);
      }

      .btn-secondary {
        background-color: transparent;
        color: var(--slate-600);
        border: 1px solid var(--slate-300);
        padding: 0.6875rem 1.25rem;
      }

      .btn-secondary:hover {
        background-color: var(--slate-100);
        color: var(--slate-800);
      }
    </style>
  </head>
  <body>
    <div class="form-container">
      <a href="/products" class="back-link">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <line x1="19" y1="12" x2="5" y2="12"></line>
          <polyline points="12 19 5 12 12 5"></polyline>
        </svg>
        Back to Products
      </a>

      <div class="card">
        <div class="card-header">
          <h1>Add New Product</h1>
          <p>Provide product details, pricing, and initial inventory stock.</p>
        </div>

        <c:set
          var="errors"
          value="${requestScope['org.springframework.validation.BindingResult.product']}"
        />
        <c:if test="${not empty errors and errors.errorCount > 0}">
          <div class="error-summary">
            <div class="error-summary-title">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
              </svg>
              Please correct the following errors:
            </div>
            <ul class="error-list">
              <c:forEach var="err" items="${errors.allErrors}">
                <li><c:out value="${err.defaultMessage}" /></li>
              </c:forEach>
            </ul>
          </div>
        </c:if>

        <form method="post" action="/products">
          <div class="form-group">
            <label for="name">Product Name</label>
            <div class="input-wrapper">
              <input
                id="name"
                name="name"
                type="text"
                placeholder="e.g. Wireless Noise-Cancelling Headphones"
                value="<c:out value='${product.name}'/>"
              />
            </div>
            <div class="field-hint">Maximum 80 characters. Required.</div>
          </div>

          <div class="form-group">
            <label for="price">Net Price</label>
            <div class="input-wrapper">
              <input
                id="price"
                name="price"
                type="number"
                step="0.01"
                placeholder="0.00"
                value="${product.price}"
              />
            </div>
            <div class="field-hint">
              Must be at least 0.01. Tax will be applied automatically.
            </div>
          </div>

          <div class="form-group">
            <label for="stock">Stock Quantity</label>
            <div class="input-wrapper">
              <input
                id="stock"
                name="stock"
                type="number"
                placeholder="0"
                value="${product.stock}"
              />
            </div>
            <div class="field-hint">
              Initial inventory units (0 or positive integer).
            </div>
          </div>

          <div class="actions-group">
            <button type="submit" class="btn btn-primary">
              Create Product
            </button>
            <a href="/products" class="btn btn-secondary">Cancel</a>
          </div>
        </form>
      </div>
    </div>
  </body>
</html>
