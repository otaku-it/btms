INSERT INTO village_profile (
    name, location, eyebrow, slogan, intro_lead, intro_body,
    land_title, land_description, map_url
) VALUES (
    '碧潭村',
    '安徽省池州市石台县仙寓镇',
    '秋浦河源头 · 仙寓镇东南',
    '一湾碧水穿村过，万亩青山抱人家。',
    '公信河流经皖南群山，在村边汇成一处碧绿深潭，“碧潭”由此得名。村庄位于安徽省池州市石台县仙寓镇东南部，也是秋浦河源头的一部分。',
    '河流穿村，溪涧纵横。19000余亩山场从村舍背后层层展开，2400亩茶园大多生长在海拔500米以上的云雾间。这里被认定为天然富硒土地，山、水、茶与日常生活共同构成了碧潭的底色。',
    '山高雾长，茶香自远',
    '碧潭村的茶园有九成分布在海拔500米以上。春日采茶、夏日听溪、秋日看山，最动人的从来不是赶景点，而是顺着山里的时辰慢下来。',
    'https://map.baidu.com/search/%E5%AE%89%E5%BE%BD%E7%9C%81%E6%B1%A0%E5%B7%9E%E5%B8%82%E7%9F%B3%E5%8F%B0%E5%8E%BF%E4%BB%99%E5%AF%93%E9%95%87%E7%A2%A7%E6%BD%AD%E6%9D%91/'
);

INSERT INTO site_stat (display_value, label, sort_order) VALUES
    ('6', '公里距仙寓镇', 1),
    ('10', '个村民组', 2),
    ('2400', '亩高山茶园', 3);

INSERT INTO journey_stop (duration, title, description, tag, image_url, image_alt, sort_order) VALUES
    ('建议 40 分钟', '沿公信河慢行', '从亲水步道开始，看河水贴着村庄流过。清晨与傍晚光线柔和，更适合散步和拍照。', '亲水 · 散步', '/assets/village-river.webp', '公信河与碧潭村民居', 1),
    ('建议 60 分钟', '穿行村巷人家', '白墙村舍顺着地势铺开。放慢脚步，也请为当地居民保留安静、真实的生活空间。', '村巷 · 人文', '/assets/mountain-village.webp', '仙寓山林间的皖南村舍', 2),
    ('建议 90 分钟', '去高处看云与茶', '从村庄向山里延伸，云雾、茶园与远山逐渐打开。雨后路滑，出发前请先留意天气。', '茶园 · 远眺', '/assets/xianyu-clouds.webp', '仙寓山观景处翻涌的云海', 3);

INSERT INTO season_content (code, name, period, title, description, sight, note, sort_order) VALUES
    ('spring', '春', '三月至五月', '新茶与新绿，一起醒来', '薄雾仍在山腰，茶园已经染上明亮的新绿。春雨过后沿村慢行，空气里都是草木清香。', '看点：春茶、山雾', '提示：备轻便雨具', 1),
    ('summer', '夏', '六月至八月', '溪水清凉，树影正浓', '公信河水色清亮，溪涧与山林送来凉意。适合清晨出门，午后在村中安静歇脚。', '看点：溪流、浓荫', '提示：留意山区阵雨', 2),
    ('autumn', '秋', '九月至十一月', '稻田明亮，远山有层次', '村庄周围的田野转为金黄，空气也变得清爽。傍晚的斜阳沿河谷落下，最适合远眺。', '看点：秋田、夕照', '提示：早晚温差较大', 3),
    ('winter', '冬', '十二月至二月', '山色沉静，人间有烟火', '叶落之后，村庄与山势显出清晰轮廓。偶遇雾凇或薄雪，更能体会皖南山村的安静。', '看点：雾凇、村烟', '提示：注意路面结冰', 4);

INSERT INTO gallery_item (title, image_url, image_alt, scope, layout, sort_order) VALUES
    ('碧潭村全景', '/assets/hero-village.webp', '群山、公信河与田野环绕的碧潭村', '碧潭村实景', 'wide', 1),
    ('仙寓山居', '/assets/mountain-village.webp', '山林之间的皖南村舍', '仙寓山周边风貌', 'tall', 2),
    ('公信河畔', '/assets/riverwalk.webp', '碧潭村公信河畔亲水步道', '碧潭村实景', 'standard', 3),
    ('仙寓云海', '/assets/xianyu-clouds.webp', '仙寓山观景处翻涌的云海', '仙寓山周边风貌', 'standard', 4);

INSERT INTO guide_item (title, content, sort_order) VALUES
    ('在哪里', '安徽省池州市石台县仙寓镇东南部，距仙寓镇政府所在地约6公里。', 1),
    ('怎么去', '山村公共交通信息有限，建议自驾或提前联系当地接驳。导航搜索“石台县仙寓镇碧潭村”。', 2),
    ('带什么', '防滑步行鞋、轻便雨具和饮用水。山区天气变化较快，进入茶园与村民生活区前请先征得同意。', 3);
