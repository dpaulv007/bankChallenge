# Build stage
FROM node:18-alpine AS build
WORKDIR /app

# Dependencias
COPY package.json ./
RUN npm install

# Código y config
COPY angular.json jest.config.js setup-jest.ts tsconfig*.json ./
COPY src ./src

# Compilar para producción
RUN npm run build

# Serve stage
FROM nginx:1.27-alpine

# Copia el directorio del build
COPY --from=build /app/dist/bank-challenge-angular /usr/share/nginx/html

# Configuración nginx para SPA con proxy API
RUN echo 'server {' > /etc/nginx/conf.d/default.conf && \
    echo '  listen 80;' >> /etc/nginx/conf.d/default.conf && \
    echo '  server_name localhost;' >> /etc/nginx/conf.d/default.conf && \
    echo '  root /usr/share/nginx/html;' >> /etc/nginx/conf.d/default.conf && \
    echo '  index index.html;' >> /etc/nginx/conf.d/default.conf && \
    echo '  location /api/ {' >> /etc/nginx/conf.d/default.conf && \
    echo '    proxy_pass http://api:8080/api/;' >> /etc/nginx/conf.d/default.conf && \
    echo '    proxy_set_header Host $host;' >> /etc/nginx/conf.d/default.conf && \
    echo '    proxy_set_header X-Real-IP $remote_addr;' >> /etc/nginx/conf.d/default.conf && \
    echo '    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;' >> /etc/nginx/conf.d/default.conf && \
    echo '  }' >> /etc/nginx/conf.d/default.conf && \
    echo '  location / {' >> /etc/nginx/conf.d/default.conf && \
    echo '    try_files $uri $uri/ /index.html;' >> /etc/nginx/conf.d/default.conf && \
    echo '  }' >> /etc/nginx/conf.d/default.conf && \
    echo '}' >> /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
