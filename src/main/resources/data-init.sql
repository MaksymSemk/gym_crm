-- 1. Populate training_types
INSERT INTO training_types (id, name) VALUES
                                          (1, 'Fitness'),
                                          (2, 'Yoga'),
                                          (3, 'Zumba'),
                                          (4, 'Weightlifting'),
                                          (5, 'Cardio');

-- 2. Populate users (Shared entity for Trainers and Trainees)
INSERT INTO users (id, first_name, last_name, username, password, is_active) VALUES
                                                                                 ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'John', 'Doe', 'John.Doe', 'pass123!', true),
                                                                                 ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Jane', 'Smith', 'Jane.Smith', 'secure456!', true),
                                                                                 ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 'Alex', 'Turner', 'Alex.Turner', 'trainer1!', true),
                                                                                 ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 'Emma', 'Watson', 'Emma.Watson', 'trainer2!', true),
                                                                                 ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b13', 'Bruce', 'Wayne', 'Bruce.Wayne', 'batman007', false);

-- 3. Populate trainers
INSERT INTO trainers (id, user_id, training_type_id) VALUES
                                                         ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 1), -- Alex (Fitness)
                                                         ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c12', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 2), -- Emma (Yoga)
                                                         ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c13', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b13', 4); -- Bruce (Weightlifting)

-- 4. Populate trainees
INSERT INTO trainees (id, user_id, date_of_birth, address) VALUES
                                                               ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '1995-05-15', '123 Main St, New York'),
                                                               ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d12', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', '1992-08-22', '456 Elm St, Boston');

-- 5. Populate trainer_trainee (ManyToMany relationship table)
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES
                                                         ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d11', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11'), -- John with Alex
                                                         ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d11', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c12'), -- John with Emma
                                                         ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d12', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11'); -- Jane with Alex

-- 6. Populate trainings
INSERT INTO trainings (id, trainee_id, trainer_id, training_type_id, training_date, training_name, training_duration) VALUES
                                                                                                                          ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380e11', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380d11', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 1, '2026-07-10', 'Morning Core Blast', 60),
                                                                                                                          ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380e12', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380d11', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c12', 2, '2026-07-11', 'Flexibility Mastery', 90),
                                                                                                                          ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380e13', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380d12', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 1, '2026-07-12', 'Intro to Cardio-Fitness', 45);