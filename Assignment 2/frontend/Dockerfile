# Etapa 1: Construirea aplicației
FROM node:18 AS build

# Setează directorul de lucru în container
WORKDIR /app

# Copiază package.json și package-lock.json în container
COPY package*.json ./

# Instalează dependențele
RUN npm install

# Copiază tot codul aplicației în container
COPY . .

# Rulează build-ul aplicației
RUN npm run build

# Etapa 2: Servirea aplicației
FROM nginx:alpine

# Șterge fișierele implicite ale Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copiază fișierele construite de la etapa 1 în folderul Nginx
COPY --from=build /app/build /usr/share/nginx/html

# Copiază configurația personalizată de Nginx (opțional)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expune portul pe care rulează Nginx
EXPOSE 80

# Pornește serverul Nginx
CMD ["nginx", "-g", "daemon off;"]
