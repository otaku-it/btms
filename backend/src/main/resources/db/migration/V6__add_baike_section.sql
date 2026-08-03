ALTER TABLE village_profile
    ADD COLUMN baike_title VARCHAR(160) NOT NULL DEFAULT '碧潭村概况' AFTER intro_image_url,
    ADD COLUMN baike_summary TEXT NULL AFTER baike_title,
    ADD COLUMN baike_source_url VARCHAR(500) NOT NULL DEFAULT 'https://baike.baidu.com/item/%E7%A2%A7%E6%BD%AD%E6%9D%91/8160870' AFTER baike_summary;

UPDATE village_profile
SET baike_summary = '碧潭村隶属于安徽省池州市石台县仙寓镇，位于仙寓镇东南部，距镇政府所在地约6公里。全村辖10个村民组，山场面积19000余亩，茶园面积2400亩，其中九成茶园分布在海拔500米以上。村域溪涧纵横，天然富硒土地与高山云雾共同孕育了当地茶产业。'
WHERE baike_summary IS NULL;

ALTER TABLE village_profile
    MODIFY COLUMN baike_summary TEXT NOT NULL;
