#!/bin/bash
set -e

DOMAIN="ec2-18-234-109-220.compute-1.amazonaws.com"
EMAIL="ysaber201@gmail.com"

echo "=== Step 1: Starting services (HTTP only) ==="
docker compose up -d --build

echo "=== Waiting for nginx to be ready ==="
sleep 5

echo "=== Step 2: Obtaining Let's Encrypt certificate ==="
docker compose run --rm certbot certonly \
  --webroot \
  --webroot-path=/var/www/certbot \
  --email "$EMAIL" \
  --agree-tos \
  --no-eff-email \
  -d "$DOMAIN"

echo "=== Step 3: Updating nginx config to enable HTTPS ==="
cat > nginx/nginx.conf << EOF
events {}

http {
    server {
        listen 80;
        server_name $DOMAIN;

        location /.well-known/acme-challenge/ {
            root /var/www/certbot;
        }

        location / {
            return 301 https://\$host\$request_uri;
        }
    }

    server {
        listen 443 ssl;
        server_name $DOMAIN;

        ssl_certificate     /etc/letsencrypt/live/$DOMAIN/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/$DOMAIN/privkey.pem;
        ssl_protocols       TLSv1.2 TLSv1.3;
        ssl_ciphers         HIGH:!aNULL:!MD5;

        location / {
            proxy_pass         http://app:8080;
            proxy_set_header   Host              \$host;
            proxy_set_header   X-Real-IP         \$remote_addr;
            proxy_set_header   X-Forwarded-For   \$proxy_add_x_forwarded_for;
            proxy_set_header   X-Forwarded-Proto \$scheme;
        }
    }
}
EOF

echo "=== Step 4: Reloading nginx with SSL config ==="
docker compose exec nginx nginx -s reload

echo "=== Done! Visit https://$DOMAIN ==="
