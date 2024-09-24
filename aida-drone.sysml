package AIDA_Drone {
    import SEIM::**;

    package 'Constituent Analysis' {
        part Drone : LogicalConstituent {
            part Structure : LogicalConstituent {
                in port 'CP 1' : ConstituentPort;
                in port 'CP 2' : ConstituentPort;
            }
            part 'Flight control and monitoring system' : LogicalConstituent {
                in port 'CP 4' : ConstituentPort;
                in port 'CP 5' : ConstituentPort;
                in port 'CP 7' : ConstituentPort;
                in port 'CP 8' : ConstituentPort;
                in port 'CP 11' : ConstituentPort;
                out port 'CP 9' : ConstituentPort;
                out port 'CP 10' : ConstituentPort;
            }
            part Payload : LogicalConstituent {
                in port 'CP 2' : ConstituentPort;
                in port 'CP 5' : ConstituentPort;
                in port 'CP 6' : ConstituentPort;
                in port 'CP 7' : ConstituentPort;
                out port 'CP 3' : ConstituentPort;
            }
            part 'Propulsion system' : LogicalConstituent {
                in port 'CP 2' : ConstituentPort;
                out port 'CP 3' : ConstituentPort;
            }

            interface 'Actuators command' : LogicalFlow {
                connect source ::> Drone.'Flight control and monitoring system'.'CP 10'
                    to target ::> Drone.'Propulsion system'.'CP 2';
            }
            interface 'Actuators thrust and torque' : LogicalFlow {
                connect source ::> Drone.'Propulsion system'.'CP 3' to target ::> Drone.Structure.'CP 2';
            }
            interface 'Payload command link' : LogicalFlow {
                connect source ::> Drone.'Flight control and monitoring system'.'CP 9'
                    to target ::> Drone.Payload.'CP 6';
            }
        }

        allocation 'esa:0019c981-6309-31a0-be6f-ea230e60b1a6' : AllocatedFunction {
            allocate function ::> 'Functionnal Analysis'::'Provide photos and videos' to component ::> Drone.Payload;
        }
        allocation 'esa:45097c3a-0cac-3a0b-ba72-a183c81000c2' : AllocatedFunction {
            allocate function ::> 'Functionnal Analysis'::'Control drone position and speed'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Detect and mitigate failures'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Provide direct remote identification information'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Provide drone navigation data'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Control drone attitude and thrust'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Manage mission'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Duplicate position'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Select control mode'
                to component ::> Drone.'Flight control and monitoring system';
            allocate function ::> 'Functionnal Analysis'::'Duplicate attitude'
                to component ::> Drone.'Flight control and monitoring system';
        }
        allocation 'esa:64415266-2bc8-3fe7-bc90-3c62b46f20d7' : AllocatedFunction {
            allocate function ::> 'Functionnal Analysis'::'Reconstitute global thrust and torque'
                to component ::> Drone.Structure;
        }
        allocation 'esa:f816f4fe-59a4-3997-aceb-07d0041a5a42' : AllocatedFunction {
            allocate function ::> 'Functionnal Analysis'::'Control propeller 1'
                to component ::> Drone.'Propulsion system';
            allocate function ::> 'Functionnal Analysis'::'Control propeller 2'
                to component ::> Drone.'Propulsion system';
            allocate function ::> 'Functionnal Analysis'::'Control propeller 3'
                to component ::> Drone.'Propulsion system';
            allocate function ::> 'Functionnal Analysis'::'Control propeller 4'
                to component ::> Drone.'Propulsion system';
        }
    }

    package 'Functionnal Analysis' {
        action <LogFun_1> 'Provide direct remote identification information' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 2' : FunctionPort;
            in port 'FIP 3' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
        }
        action <LogFun_3> 'Provide drone navigation data' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 12' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
            out port 'FOP 3' : FunctionPort;
            out port 'FOP 4' : FunctionPort;
            out port 'FOP 5' : FunctionPort;
            out port 'FOP 6' : FunctionPort;
        }
        action <LogFun_4_4> 'Manage mission' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 2' : FunctionPort;
            in port 'FIP 4' : FunctionPort;
            in port 'FIP 5' : FunctionPort;
            in port 'FIP 6' : FunctionPort;
            in port 'FIP 7' : FunctionPort;
            in port 'FIP 9' : FunctionPort;
            in port 'FIP 10' : FunctionPort;
            in port 'FIP 13' : FunctionPort;
            out port 'FOP 2' : FunctionPort;
            out port 'FOP 3' : FunctionPort;
            out port 'FOP 7' : FunctionPort;
        }
        action <LogFun_4_5> 'Control drone position and speed' : Function {
            out port 'FOP 1' : FunctionPort;
            in port 'FIP 12' : FunctionPort;
            in port 'FIP 7' : FunctionPort;
            in port 'FIP 17' : FunctionPort;
            in port 'FIP 9' : FunctionPort;
            in port 'FIP 8' : FunctionPort;
            in port 'FIP 6' : FunctionPort;
        }
        action <LogFun_4_8> 'Select control mode' : Function {
            in port 'FIP 3' : FunctionPort;
            in port 'FIP 15' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
        }
        action <LogFun_4_9> 'Control drone attitude and thrust' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 2' : FunctionPort;
            in port 'FIP 3' : FunctionPort;
            in port 'FIP 4' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
            out port 'FOP 2' : FunctionPort;
            out port 'FOP 3' : FunctionPort;
            out port 'FOP 4' : FunctionPort;
        }
        action <LogFun_4_10> 'Reconstitute global thrust and torque' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 2' : FunctionPort;
            in port 'FIP 3' : FunctionPort;
            in port 'FIP 4' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
        }
        action <LogFun_5_2> 'Provide photos and videos' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 3' : FunctionPort;
            in port 'FIP 4' : FunctionPort;
            in port 'FIP 5' : FunctionPort;
            out port 'FOP 2' : FunctionPort;
            out port 'FOP 3' : FunctionPort;
            out port 'FOP 5' : FunctionPort;
        }
        action <LogFun_6_1> 'Detect and mitigate failures' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 2' : FunctionPort;
            in port 'FIP 4' : FunctionPort;
            in port 'FIP 5' : FunctionPort;
            in port 'FIP 6' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
            out port 'FOP 3' : FunctionPort;
            out port 'FOP 5' : FunctionPort;
        }

        action 'Duplicate position' : Function {
            in port 'FIP 1' : FunctionPort;
            in port 'FIP 7' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
            out port 'FOP 7' : FunctionPort;
        }
        action 'Duplicate attitude' : Function {
            in port 'FIP 16' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
            out port 'FOP 2' : FunctionPort;
        }

        action <SF1_1> 'Control propeller 1' : Function {
            in port 'FIP 2' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
        }
        action <SF1_2> 'Control propeller 2' : Function {
            in port 'FIP 1' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
        }
        action <SF1_3> 'Control propeller 3' : Function {
            in port 'FIP 1' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
        }
        action <SF1_4> 'Control propeller 4' : Function {
            in port 'FIP 1' : FunctionPort;
            out port 'FOP 1' : FunctionPort;
        }

        interface 'Auto Visual information acquisition command' : FunctionalFlow {
            connect source ::> 'Manage mission'.'FOP 3' to target ::> 'Provide photos and videos'.'FIP 5';
        }
        interface 'Selected control mode' : FunctionalFlow {
            connect source ::> 'Select control mode'.'FOP 1' to target ::> 'Control drone position and speed'.'FIP 8';
        }
        interface 'Drone speed 0' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 5'
                to target ::> 'Detect and mitigate failures'.'FIP 6';
        }
        interface 'Drone speed 1' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 5'
                to target ::> 'Control drone position and speed'.'FIP 17';
        }
        interface 'Drone position 0' : FunctionalFlow {
            connect source ::> 'Duplicate position'.'FOP 1' to target ::> 'Control drone position and speed'.'FIP 7';
        }
        interface 'Drone attitude 0' : FunctionalFlow {
            connect source ::> 'Duplicate attitude'.'FOP 2' to target ::> 'Control drone attitude and thrust'.'FIP 1';
        }
        interface 'Drone attitude 1' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 1'
                to target ::> 'Provide direct remote identification information'.'FIP 2';
        }
        interface 'Drone attitude 2' : FunctionalFlow {
            connect source ::> 'Duplicate attitude'.'FOP 1' to target ::> 'Control drone position and speed'.'FIP 9';
        }
        interface 'Drone attitude 3' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 1'
                to target ::> 'Detect and mitigate failures'.'FIP 5';
        }
        interface 'Return To Launch Engagement' : FunctionalFlow {
            connect source ::> 'Detect and mitigate failures'.'FOP 3' to target ::> 'Manage mission'.'FIP 5';
        }
        interface 'Drone position 1' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 6'
                to target ::> 'Detect and mitigate failures'.'FIP 2';
        }
        interface 'Drone position 2' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 6' to target ::> 'Duplicate position'.'FIP 7';
        }
        interface 'Drone position 3' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 3'
                to target ::> 'Provide direct remote identification information'.'FIP 1';
        }
        interface 'Drone position 4' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 6'
                to target ::> 'Provide direct remote identification information'.'FIP 1';
        }
        interface 'Actuator 1 command' : FunctionalFlow {
            connect source ::> 'Control drone attitude and thrust'.'FOP 1' to target ::> 'Control propeller 1'.'FIP 2';
        }
        interface 'Actuator 2 command' : FunctionalFlow {
            connect source ::> 'Control drone attitude and thrust'.'FOP 2' to target ::> 'Control propeller 2'.'FIP 1';
        }
        interface 'Actuator 3 command' : FunctionalFlow {
            connect source ::> 'Control drone attitude and thrust'.'FOP 3' to target ::> 'Control propeller 3'.'FIP 1';
        }
        interface 'Actuator 4 command' : FunctionalFlow {
            connect source ::> 'Control drone attitude and thrust'.'FOP 4' to target ::> 'Control propeller 4'.'FIP 1';
        }
        interface 'Acquisition confirmation' : FunctionalFlow {
            connect source ::> 'Provide photos and videos'.'FOP 2' to target ::> 'Manage mission'.'FIP 13';
        }
        interface 'Actuator 1 thrust and torque' : FunctionalFlow {
            connect source ::> 'Control propeller 1'.'FOP 1'
                to target ::> 'Reconstitute global thrust and torque'.'FIP 1';
        }
        interface 'Actuator 2 thrust and torque' : FunctionalFlow {
            connect source ::> 'Control propeller 2'.'FOP 1'
                to target ::> 'Reconstitute global thrust and torque'.'FIP 2';
        }
        interface 'Actuator 3 thrust and torque' : FunctionalFlow {
            connect source ::> 'Control propeller 3'.'FOP 1'
                to target ::> 'Reconstitute global thrust and torque'.'FIP 3';
        }
        interface 'Actuator 4 thrust and torque' : FunctionalFlow {
            connect source ::> 'Control propeller 4'.'FOP 1'
                to target ::> 'Reconstitute global thrust and torque'.'FIP 4';
        }
        interface 'Environment information' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 3' to target ::> 'Manage mission'.'FIP 9';
        }
        interface 'Failure control mode request' : FunctionalFlow {
            connect source ::> 'Detect and mitigate failures'.'FOP 5' to target ::> 'Select control mode'.'FIP 15';
        }
        interface 'Auto attitude and thrust commands' : FunctionalFlow {
            connect source ::> 'Control drone position and speed'.'FOP 1'
                to target ::> 'Control drone attitude and thrust'.'FIP 4';
        }
        interface 'Drone position' : FunctionalFlow {
            connect source ::> 'Duplicate position'.'FOP 1' to target ::> 'Manage mission'.'FIP 7';
        }
        interface 'Selected control mode 0' : FunctionalFlow {
            connect source ::> 'Select control mode'.'FOP 1' to target ::> 'Control drone attitude and thrust'.'FIP 2';
        }
        interface 'Position demand' : FunctionalFlow {
            connect source ::> 'Manage mission'.'FOP 2' to target ::> 'Control drone position and speed'.'FIP 6';
        }
        interface 'Drone attitude' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 1' to target ::> 'Duplicate attitude'.'FIP 16';
        }
        interface 'Selected control mode 1' : FunctionalFlow {
            connect source ::> 'Select control mode'.'FOP 1' to target ::> 'Manage mission'.'FIP 10';
        }
        interface 'Drone speed' : FunctionalFlow {
            connect source ::> 'Provide drone navigation data'.'FOP 5'
                to target ::> 'Provide direct remote identification information'.'FIP 3';
        }
    }
}