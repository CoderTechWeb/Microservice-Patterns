Authentication & Authorization Guide

🔐 User Login & Access Control

1️⃣ Login as testuser (ROLE_USER)

{
  "username": "testuser",
  "password": "password123"
}

Access Permissions:

✅ Allowed: /order/user/orders

❌ Forbidden (403): /order/admin/orders

2️⃣ Login as adminuser (ROLE_ADMIN)

{
  "username": "adminuser",
  "password": "admin123"
}

Access Permissions:

✅ Allowed: /order/admin/orders

❌ Forbidden (403): /order/user/orders

🌍 OAuth2 Login Endpoints

You can authenticate using Google or GitHub OAuth2 login:

Google Login: Click Here

GitHub Login: Click Here

Use these endpoints to log in via OAuth2 and receive JWT tokens for accessing secured resources.
